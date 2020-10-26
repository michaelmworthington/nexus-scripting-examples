import org.sonatype.nexus.common.entity.*
import org.sonatype.nexus.security.*
import org.sonatype.nexus.security.authz.*
import org.sonatype.nexus.selector.*

import com.google.common.collect.ImmutableMap

// use container.lookup to fetch internal APIs we need to use
SelectorManager selectorManager = container.lookup(SelectorManager.class.name)
SecuritySystem securitySystem = container.lookup(SecuritySystem.class.name)
AuthorizationManager authorizationManager = securitySystem.getAuthorizationManager('default')

// create content selector (if not already present)
def selectorConfig = new SelectorConfiguration(
        name: 'mycompany-custom-selector',
        type: 'jexl',
        description: 'selector for my custom package',
        attributes: ['expression': 'coordinate.groupId =^ "com.mycompany"']
)
if (selectorManager.browse().find { it -> it.name == selectorConfig.name } == null) {
  selectorManager.create(selectorConfig)
}

// create snapshot and release repositories
def snapshotName = "mycompany-maven-snapshots"
def releaseName = "mycompany-maven-releases"
repository.createMavenHosted(snapshotName, 'default', false,
                             org.sonatype.nexus.repository.maven.VersionPolicy.SNAPSHOT,
                             org.sonatype.nexus.repository.storage.WritePolicy.ALLOW)
repository.createMavenHosted(releaseName, 'default', false,
                             org.sonatype.nexus.repository.maven.VersionPolicy.RELEASE,
                             org.sonatype.nexus.repository.storage.WritePolicy.ALLOW_ONCE)

// create content selector privilege for release repo
def releaseProperties = ImmutableMap.builder()
        .put("content-selector", selectorConfig.name)
        .put("repository", releaseName)
        .put("actions", "browse,read,edit")
        .build()
def releasePrivilege = new org.sonatype.nexus.security.privilege.Privilege(
        id: "mycompany-release-priv",
        version: '',
        name: "mycompany-release-priv",
        description: "Content Selector Release privilege",
        type: "repository-content-selector",
        properties: releaseProperties
)
authorizationManager.addPrivilege(releasePrivilege)

// create content selector privilege for snapshot repo
def snapshotProperties = ImmutableMap.builder()
        .put("content-selector", selectorConfig.name)
        .put("repository", snapshotName)
        .put("actions", "browse,read,edit")
        .build()
def snapshotPrivilege = new org.sonatype.nexus.security.privilege.Privilege(
        id: "mycompany-snapshot-priv",
        version: '',
        name: "mycompany-snapshot-priv",
        description: "Content Selector Snapshot privilege",
        type: "repository-content-selector",
        properties: snapshotProperties
)
authorizationManager.addPrivilege(snapshotPrivilege)

// create a role with the snapshot and release privileges
def role = new org.sonatype.nexus.security.role.Role(
        roleId: "mycompany-role",
        source: "Nexus",
        name: "mycompany-role",
        description: "My Company Role",
        readOnly: false,
        privileges: [ snapshotPrivilege.id, releasePrivilege.id ],
        roles: []
)
authorizationManager.addRole(role)

// add a local user account with the role
security.addUser("devuser",
                 "Delilah", "Developer",
                 "companydev@mycompany.com", true,
                 "devpassword", [ role.roleId ])
//https://gist.github.com/rgl/8cbc6bbe128428be7866338be464cd1c

// see https://books.sonatype.com/nexus-book/3.0/reference/scripting.html
// see https://github.com/sonatype/nexus-book-examples/tree/nexus-3.0.x/scripting/nexus-script-example

import groovy.json.JsonOutput
import org.sonatype.nexus.security.user.UserSearchCriteria
import org.sonatype.nexus.security.authc.apikey.ApiKeyStore
import org.sonatype.nexus.security.realm.RealmManager
import org.apache.shiro.subject.SimplePrincipalCollection
import org.sonatype.nexus.scheduling.TaskScheduler
import org.sonatype.nexus.scheduling.schedule.Daily

// set the base url. this is used when sending emails.
// see https://books.sonatype.com/nexus-book/3.0/reference/admin.html#admin-base-url
core.baseUrl("https://" + java.net.InetAddress.getLocalHost().getCanonicalHostName())


// see http://stackoverflow.com/questions/8138164/groovy-generate-random-string-from-given-character-set
def random(String alphabet, int n) {
  new Random().with {
    (1..n).collect { alphabet[nextInt(alphabet.length())] }.join()
  }
}
jenkinsPassword = random((('A'..'Z')+('a'..'z')+('0'..'9')).join(), 16)
//jenkinsPassword = "password"


// enable the NuGet API-Key Realm.
realmManager = container.lookup(RealmManager.class.getName())
realmManager.enableRealm("NuGetApiKey")


// the intent is to get or create an NuGet API Key like the one we can see on the user page:
// http://nexus.example.com:8081/#user/nugetapitoken.
def getOrCreateNuGetApiKey(String userName) {
  realmName = "NexusAuthenticatingRealm"
  apiKeyDomain = "NuGetApiKey"
  principal = new SimplePrincipalCollection(userName, realmName)
  keyStore = container.lookup(ApiKeyStore.class.getName())
  apiKey = keyStore.getApiKey(apiKeyDomain, principal)
  if (apiKey == null) {
    apiKey = keyStore.createApiKey(apiKeyDomain, principal)
  }
  return apiKey.toString()
}

def getOrCreateUserToken(String userName) {

  //AuthTicketServiceImpl

  //com.sonatype.nexus.usertoken.plugin.rest.CurrentResource utr = container.lookup(CurrentResource.class.getName());
  //utr.get("")

  //com.sonatype.nexus.usertoken.plugin.UserTokenRecord record = getUserTokens().current(true);

  //import com.sonatype.nexus.usertoken.plugin.internal.UserTokenServiceImpl;
  //import com.sonatype.nexus.usertoken.plugin.UserTokenRecord;

  UserTokenServiceImpl utsi = container.lookup(UserTokenServiceImpl.class.getName());
  UserTokenRecord utr = utsi.get("foo");

  log.info("UTR Name: " + utr.getUserToken().getNameCode());
  log.info("UTR Pass: " + utr.getUserToken().getPassCode());
  log.info("UTR Created: " + utr.getCreated());


  PrincipalCollection p = new SimplePrincipalCollection();


  import org.sonatype.nexus.security.SecurityHelper;

  SecurityHelper sh = container.lookup(SecurityHelper.class.getName());

  log.info("PR: " + sh.subject().getPrincipals())

}


// create some users in the deployer role.
// see https://github.com/sonatype/nexus-book-examples/blob/nexus-3.0.x/scripting/complex-script/security.groovy#L38
def addDeployerUser(firstName, lastName, email, userName, password) {
  if (!security.securitySystem.listRoles().any { it.getRoleId() == "deployer" }) {
    privileges = [
            "nx-search-read",
            "nx-repository-view-*-*-read",
            "nx-repository-view-*-*-browse",
            "nx-repository-view-*-*-add",
            "nx-repository-view-*-*-edit",
            "nx-apikey-all"]
    security.addRole("deployer", "deployer", "deployment on all repositories", privileges, [])
  }
  try {
    user = security.securitySystem.getUser(userName);
  } catch (org.sonatype.nexus.security.user.UserNotFoundException e) {
    user = security.addUser(userName, firstName, lastName, email, true, password, ["deployer"])
  }
  nuGetApiKey = getOrCreateNuGetApiKey(userName)
}
addDeployerUser("Jenkins", "Doe", "jenkins@example.com", "jenkins", jenkinsPassword)
addDeployerUser("Alice", "Doe", "alice.doe@example.com", "alice.doe", "password")
addDeployerUser("Bob", "Doe", "bob.doe@example.com", "bob.doe", "password")


// get the jenkins NuGet API Key.
jenkinsNuGetApiKey = getOrCreateNuGetApiKey("jenkins")


// schedule a task to remove old snapshots from the maven-snapshots repository.
// see https://github.com/sonatype/nexus-public/blob/555cc59e7fa659c0a1a4fbc881bf3fcef0e9a5b6/components/nexus-scheduling/src/main/java/org/sonatype/nexus/scheduling/TaskScheduler.java
// see https://github.com/sonatype/nexus-public/blob/555cc59e7fa659c0a1a4fbc881bf3fcef0e9a5b6/plugins/nexus-coreui-plugin/src/main/java/org/sonatype/nexus/coreui/TaskComponent.groovy
taskScheduler = (TaskScheduler)container.lookup(TaskScheduler.class.getName())
taskConfiguration = taskScheduler.createTaskConfigurationInstance("repository.maven.remove-snapshots")
taskConfiguration.name = "remove old snapshots from the maven-snapshots repository"
// NB to see the available properties uncomment the tasksDescriptors property from JsonOutput.toJson at the end of this script.
taskConfiguration.setString("repositoryName", "maven-snapshots")
taskConfiguration.setString("minimumRetained", "1")
taskConfiguration.setString("snapshotRetentionDays", "30")
// TODO taskConfiguration.setAlertEmail("TODO")
taskScheduler.scheduleTask(taskConfiguration, new Daily(new Date().clearTime().next()))


realms = realmManager.getConfiguration().getRealmNames()
users = security.securitySystem.searchUsers(new UserSearchCriteria())
repositories = repository.repositoryManager.browse().collect { [name:it.name,type:it.type.value] }

return JsonOutput.toJson([
        /*tasksDescriptors: taskScheduler.taskFactory.descriptors.collect { [
            id: it.id,
            name: it.name,
            exposed: it.exposed,
            formFields: it.formFields?.collect { [
                id: it.id,
                type: it.type,
                label: it.label,
                helpText: it.helpText,
                required: it.required,
                regexValidation: it.regexValidation,
                initialValue: it.initialValue,
                ] }
        ] },*/
        jenkinsNuGetApiKey: jenkinsNuGetApiKey,
        realms: realms.sort { it },
        users: users.sort { it.userId },
        repositories: repositories.sort { it.name },
])
import org.sonatype.nexus.repository.maven.VersionPolicy

// simple example showing simple method and equivalent method with all default parameters expanded.
String blobstore = "s3ninja"

blobstoreName = blobstore + "-blobstore"
proxyName = "maven-central-" + blobstore
releaseName = "maven-releases-" + blobstore
snapshotName = "maven-snapshots-" + blobstore
groupName = "maven-public-" + blobstore

repository.createMavenProxy(proxyName,
                            "https://repo1.maven.org/maven2/",
                            blobstoreName)

repository.createMavenHosted(releaseName,
                             blobstoreName,
                             true,
                             VersionPolicy.RELEASE)

repository.createMavenHosted(snapshotName,
                             blobstoreName,
                             true,
                             VersionPolicy.SNAPSHOT)

repository.createMavenGroup(groupName,
                            [proxyName, releaseName, snapshotName],
                            blobstoreName)

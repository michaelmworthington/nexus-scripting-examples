// https://support.sonatype.com/hc/en-us/articles/115013102487-Nexus-3-Groovy-Scripts


import org.sonatype.nexus.common.app.ApplicationDirectories
import org.sonatype.nexus.repository.tools.DeadBlobFinder
import org.sonatype.nexus.repository.tools.DeadBlobResult
import groovy.json.JsonOutput
import java.text.SimpleDateFormat

def deadBlobFinder = container.lookup(DeadBlobFinder.class.name)
def applicationDirectories = container.lookup(ApplicationDirectories.class.name)
// Default location of results is the Nexus temporary directory
def resultsFileLocation = applicationDirectories.getTemporaryDirectory() as File
def results = []
// List of blobstores to look over - all are included if none specified.
def blobstores = []
log.info('DeadBlobFinder START.')

repository.repositoryManager.browse().each { repo ->
  if (blobstores.isEmpty() || repo.getConfiguration().attributes['storage']['blobStoreName'] in blobstores) {
    try {
      log.info("Looking for dead blobs in {}.", repo.name)
      results << deadBlobFinder.find(repo,false)
    }
    catch (Exception e) {
      log.warn("Exception details: {}", e.toString())
    }
  }
}
def map = results.flatten().groupBy { it.repositoryName }.collectEntries { k, v ->
  [(k): v.collect { DeadBlobResult result -> [ result.asset.toString(), result.resultState ] }]
}

File resultsFile = new File(resultsFileLocation,
                            "deadBlobResult-${new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())}.json")
resultsFile.withWriter { Writer writer ->
  writer << JsonOutput.prettyPrint(JsonOutput.toJson(map))
}

log.info('DeadBlobFinder END. Report at {}', resultsFile.absolutePath)
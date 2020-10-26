// https://support.sonatype.com/hc/en-us/articles/115009519847-Investigating-Blob-Store-and-Repository-Size-and-Space-Usage

import org.sonatype.nexus.repository.storage.StorageFacet
import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.storage.Asset
import groovy.json.JsonOutput

long min_size = 100000000

repository.repositoryManager.browse().each { Repository repo ->
  StorageFacet storageFacet = repo.facet(StorageFacet)
  log.info("#### Repository: " + repo.getName() + " ####")
  def tx = storageFacet.txSupplier().get()
  def results = [:].withDefault { 0 }
  try {
    tx.begin()

    tx.browseAssets(tx.findBucket(repo)).each { Asset asset ->
      if (asset.size() > min_size) {
        results.put(asset.name(),asset.size())
      }
    }
  } finally {
    tx.close()
  }
  def sorted = results.sort { a, b -> b.value <=> a.value }
  log.info(JsonOutput.prettyPrint(JsonOutput.toJson(sorted)))
}
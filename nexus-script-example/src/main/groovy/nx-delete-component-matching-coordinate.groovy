// https://support.sonatype.com/hc/en-us/articles/115013102487-Nexus-3-Groovy-Scripts


import org.sonatype.nexus.repository.storage.Query
import org.sonatype.nexus.repository.storage.StorageFacet

def repo = repository.repositoryManager.get('maven-releases')

StorageFacet storageFacet = repo.facet(StorageFacet)

def tx = storageFacet.txSupplier().get()

try {
  tx.begin()
  tx.findComponents(Query.builder().where('group').eq("com.foo.bar").and('version ').eq("0.9.0").build(), [repo]).each { component ->
    log.info("Deleting component ${component.group()} ${component.name()} ${component.version()}");
    tx.deleteComponent(component)
  }

  tx.commit()
}
catch (Exception e) {
  log.warn("Cleanup failed!")
  log.warn("Exception details: {}", e.toString())
  log.warn("Rolling back storage transaction")
  tx.rollback()
}
finally {
  tx.close()
}
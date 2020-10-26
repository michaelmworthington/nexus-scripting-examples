import org.sonatype.nexus.repository.storage.Asset
import org.sonatype.nexus.repository.storage.Query
import org.sonatype.nexus.repository.storage.StorageFacet

// https://support.sonatype.com/hc/en-us/articles/115015812727-Nexus-3-Groovy-Script-development-environment-setup
// to enable interactive remote debugging of groovy scripts, you need to enclose the groovy code  you wish to debug
// in a groovy class. Keep in mind some objects are not visible inside the class, so you may need to pass such objects into the class

class MyGroovyClass
{
  private final log
  private final repository

  MyGroovyClass(log, repository)
  {
    this.log = log
    this.repository = repository
  }

  String doStuff()
  {
    String repoName = 'raw-hosted';
    def repo = repository.repositoryManager.get(repoName)
    StorageFacet storageFacet = repo.facet(StorageFacet)
    def tx = storageFacet.txSupplier().get()
    try
    {
      tx.begin()
      Iterable<Asset> assets = tx.
              findAssets(Query.builder().where('name = ').param("test/csel.txt").build(), [repo])
      assets.collect {
        log.info("FOUND ASSET: ${it.name()}")
      }
    }
    finally
    {
      tx.close()
    }
  }
}


new MyGroovyClass(log, repository).doStuff()

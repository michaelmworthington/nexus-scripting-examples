import groovy.json.JsonOutput
import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.storage.Asset
import org.sonatype.nexus.repository.storage.Query
import org.sonatype.nexus.repository.storage.StorageFacet
import org.sonatype.nexus.orient.raw.RawContentFacet
import org.sonatype.nexus.repository.view.Content
import org.sonatype.nexus.transaction.UnitOfWork

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

    RawContentFacet contentFacet = repo.facet(RawContentFacet)

    try
    {
      //tx.begin()
      UnitOfWork.begin(storageFacet.txSupplier());

      Content content = contentFacet.get("test/csel.txt")
      BufferedReader br = new BufferedReader(new InputStreamReader(content.openInputStream()))

      String line = br.readLine()
      while (line != null)
      {
        log.info("FILTERING BY: " + line)
        line = br.readLine()
      }
    }
    catch (Exception e)
    {
      log.info(e.toString())
      log.error("ERROR: couldn't load filter file")
    }
    finally
    {
      //tx.close()
      UnitOfWork.end()

    }
  }
}


new MyGroovyClass(log, repository).doStuff()

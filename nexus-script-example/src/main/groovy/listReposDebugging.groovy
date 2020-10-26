import groovy.json.JsonOutput
import org.sonatype.nexus.repository.Repository

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

    List<String> urls = []

    repository.repositoryManager.browse().each { Repository repo ->
      log.info("Repository: $repo")
      urls.add(repo.name)

    }
    return JsonOutput.toJson(urls)
  }
}


new MyGroovyClass(log, repository).doStuff()

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import org.sonatype.nexus.repository.config.Configuration;

//expects json string with appropriate content to be passed in
def repo = null
if (args?.trim()) {
  repo = new JsonSlurper().parseText(args)
}

String groupRepoName = repo?.name

Configuration groupRepoConfig = repository.repositoryManager.get(groupRepoName).getConfiguration();

return JsonOutput.toJson(groupRepoConfig.getAttributes().group.memberNames)

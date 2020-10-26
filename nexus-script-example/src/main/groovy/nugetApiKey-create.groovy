import org.apache.shiro.subject.SimplePrincipalCollection
import org.sonatype.nexus.security.authc.apikey.ApiKeyStore

userName = "foo"
realmName = "NexusAuthenticatingRealm"
apiKeyDomain = "NuGetApiKey"

principal = new SimplePrincipalCollection(userName, realmName)

keyStore = container.lookup(ApiKeyStore.class.getName())

//get
apiKey = keyStore.getApiKey(apiKeyDomain, principal)

if (apiKey == null) {
  log.info("API Key was null. Creating one.")
  apiKey = keyStore.createApiKey(apiKeyDomain, principal)
}

log.info("API Key: " + apiKey.toString());

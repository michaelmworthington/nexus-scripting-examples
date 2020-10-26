import org.apache.shiro.subject.SimplePrincipalCollection
import com.sonatype.nexus.usertoken.plugin.internal.UserTokenServiceImpl;
import com.sonatype.nexus.usertoken.plugin.UserTokenRecord;

shouldCreate = true
userName = "foo"
realmName = "NexusAuthenticatingRealm"

p = new SimplePrincipalCollection(userName, realmName)

UserTokenServiceImpl utsi = container.lookup(UserTokenServiceImpl.class.getName());
//utsi.remove(userName);

UserTokenRecord utr = utsi.get(userName);

if (utr == null) {
  log.info("API Key does not exist")

  if (shouldCreate) {
    log.info("Creating API Key")
    utr = utsi.create(p);
  }
}
else {
  log.info("API Key already exists.")
}

if (utr == null){
  log.info("API Key still doesn't exist.")
}
else
{
  log.info("UTR Name: " + utr.getUserToken().getNameCode());
  log.info("UTR Pass: " + utr.getUserToken().getPassCode());
  log.info("UTR Created: " + utr.getCreated());
}


//import org.sonatype.nexus.security.SecurityHelper;
//SecurityHelper sh = container.lookup(SecurityHelper.class.getName());
//log.info("PR: " + sh.subject().getPrincipals())
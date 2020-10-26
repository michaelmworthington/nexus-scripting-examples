import com.sonatype.nexus.usertoken.plugin.internal.UserTokenServiceImpl;
import com.sonatype.nexus.usertoken.plugin.UserTokenRecord

userName = "foo"

UserTokenServiceImpl utsi = container.lookup(UserTokenServiceImpl.class.getName());
UserTokenRecord utr = utsi.get(userName);
//UserTokenRecord utr = utsi.remove(userName);
//UserTokenRecord utr = utsi.current(false);

log.info("UTR Name: " + utr.getUserToken().getNameCode());
log.info("UTR Pass: " + utr.getUserToken().getPassCode());
log.info("UTR Created: " + utr.getCreated());

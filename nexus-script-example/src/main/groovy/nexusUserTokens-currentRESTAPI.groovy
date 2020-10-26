import com.sonatype.nexus.usertoken.plugin.rest.CurrentResource;
import org.sonatype.nexus.internal.wonderland.AuthTicketServiceImpl;
import com.sonatype.nexus.usertoken.plugin.rest.model.UserTokenXO;



AuthTicketServiceImpl atsi = container.lookup(AuthTicketServiceImpl.class.getName());
CurrentResource utr = container.lookup(CurrentResource.class.getName());

String ticket = atsi.createTicket()
log.info("Ticket: " + ticket)

UserTokenXO utxo = utr.get(ticket)

log.info("UTR Name: " + utxo.getNameCode());
log.info("UTR Pass: " + utxo.getPassCode());
log.info("UTR Created: " + utxo.getCreated());

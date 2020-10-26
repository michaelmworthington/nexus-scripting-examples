import groovy.json.JsonOutput
import org.sonatype.nexus.security.user.User

List<String> userNames = []

users = security.getSecuritySystem().listUsers()
size = users.size()
log.info("User count: $size")

/*
users.each { User user ->
    log.info("Closure User: $user")
}

for (user in users)
{
    log.info("For User: $user")
    userNames.add(user.userId)
}
*/

//return JsonOutput.toJson(userNames)
return JsonOutput.toJson(users)

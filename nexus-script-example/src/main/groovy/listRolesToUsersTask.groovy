users = security.getSecuritySystem().getUserManager("LDAP").listUsers()

for (user in users)
{
    log.info("User: $user")

    for (role in user.getRoles())
    {
        log.info("    ----> $role")
    }

}

f = new File ("/tmp/test.txt")

f.writeln("hello world")


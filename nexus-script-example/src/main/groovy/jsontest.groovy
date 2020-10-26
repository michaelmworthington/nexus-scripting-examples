import groovy.json.JsonSlurper
import groovy.json.JsonOutput

//expects json string with appropriate content to be passed in
def role = null
if (args?.trim()) {
    role = new JsonSlurper().parseText(args)
}

return JsonOutput.toJson([result: 'Success!',
                          message: role?.id + " - " + role?.name + " - " + role?.description])
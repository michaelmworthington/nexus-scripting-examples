#!/bin/bash

# A simple example script that publishes a number of scripts to the Nexus Repository Manager
# and executes them.

# fail if anything errors
set -e
# fail if a function call is missing an argument
#set -u

curl_verbose=""
#curl_verbose="-v"

username=admin
password=admin123

# add the context if you are not using the root context
host=http://host.docker.internal:8083/nexus

#placeholder variable to store the script result so i can parse it outside the runScript function with jq
runScriptRespone=""

function listCurrentScripts {
	#groovy -Dgroovy.grape.report.downloads=false -Dgrape.config=grapeConfig.xml addUpdateScript.groovy -u "$username" -p "$password" -h "$host"
    runScriptRespone=`curl $curl_verbose -X GET -u $username:$password $host/service/rest/v1/script/ 2> /dev/null`
	echo ""
	echo ""
	echo ""
}

# add a script to the repository manager and run it
function addScript {
  name=$1
  file=$2
  # using grape config that points to local Maven repo and Central Repository , default grape config fails on some downloads although artifacts are in Central
  # change the grapeConfig file to point to your repository manager, if you are already running one in your organization
  #echo "groovy -Dgroovy.grape.report.downloads=true -Dgrape.config=grapeConfig.xml addUpdateScript.groovy -u $username -p $password -n $name -f $file -h $host"
  groovy -Dgroovy.grape.report.downloads=true -Dgrape.config=grapeConfig.xml addUpdateScript.groovy -u "$username" -p "$password" -n "$name" -f "$file" -h "$host"
  printf "\nPublished $file as $name\n"

  #curl -v -X POST -u $username:$password --header "Content-Type: text/plain" "$host/service/rest/v1/script/$name/run"
  #printf "\nSuccessfully executed $name script\n\n\n"
	echo ""
	echo ""
	echo ""
}

function runScript {
	name=$1
	script_args=$2

	printf "Running Integration API Script $name\n"
	echo "curl $curl_verbose -X POST -u $username:$password --header "Content-Type: text/plain" "$host/service/rest/v1/script/$name/run" -d "$script_args""

	if [ -n "$script_args" ]
	then
		runScriptRespone=`curl $curl_verbose -X POST -u $username:$password --header "Content-Type: text/plain" "$host/service/rest/v1/script/$name/run" -d "$script_args"  2> /dev/null`
	else
		runScriptRespone=`curl $curl_verbose -X POST -u $username:$password --header "Content-Type: text/plain" "$host/service/rest/v1/script/$name/run" 2> /dev/null`
	fi

	echo ""
	echo ""
	echo ""
}

function deleteScript {
	name=$1

	printf "Deleting Integration API Script $name\n"

	curl $curl_verbose -X DELETE -u $username:$password  "$host/service/rest/v1/script/$name"

	echo ""
	echo ""
	echo ""
}


echo "#############################################################################"
printf "Provisioning Integration API Scripts Starting \n" 
echo "#############################################################################"
listCurrentScripts
echo $runScriptRespone | jq-osx-amd64 -c -r '.[].name'

echo "#############################################################################"
printf "Publishing on $host\n"
echo "#############################################################################"

#scripts in this folder
#
#addScript npmBower npmAndBowerRepositories.groovy
#addScript raw rawRepositories.groovy
#addScript security security.groovy
#addScript core core.groovy

#scripts in the nexus-script-example intellij project folder
#
#addScript createmavenrepo ../nexus-script-example/src/main/groovy/maven.groovy
#addScript provision ../nexus-script-example/src/main/groovy/provision.groovy
#addScript jsontest ../nexus-script-example/src/main/groovy/jsontest.groovy
#addScript listrepos ../nexus-script-example/src/main/groovy/listRepos.groovy
#addScript listreposdebugging ../nexus-script-example/src/main/groovy/listReposDebugging.groovy
#addScript repoassetlister ../nexus-script-example/src/main/groovy/repoAssetLister.groovy
#addScript countcomponents ../nexus-script-example/src/main/groovy/countComponents.groovy
#addScript listusers ../nexus-script-example/src/main/groovy/listUsers.groovy
#addScript listrolestousers ../nexus-script-example/src/main/groovy/listRolesToUsers.groovy
#addScript rutauthadd ../nexus-script-example/src/main/groovy/rutAuthAdd.groovy
#addScript addrole ./addRole.groovy
addScript primeMultiProxyOnRemotePublish ../nexus-script-example/src/main/groovy/primeProxy/primeMultiProxyOnRemotePublish.groovy

echo "#############################################################################"
printf "Executing on $host\n"
echo "#############################################################################"

#runScript jsontest "{ \"id\": \"foo\", \"name\": \"bar\", \"description\": \"baz\", \"privilegeIds\": [\"nx-all\"], \"roleIds\": [\"nx-admin\"] }"
#echo $runScriptRespone | jq-osx-amd64 -c -r  '.'
#echo $runScriptRespone | jq-osx-amd64 -c -r  '.result' | jq-osx-amd64 '.[]'

#runScript listrepos
#echo $runScriptRespone | jq-osx-amd64 -c -r  '.result' | jq-osx-amd64 '.[]'

#runScript listreposdebugging
#echo $runScriptRespone | jq-osx-amd64 -c -r  '.result' | jq-osx-amd64 '.[]'

#runScript listusers
#echo $runScriptRespone | jq-osx-amd64 -c -r  '.result' | jq-osx-amd64 '.[]'

#runScript repoassetlister "{ \"repoName\": \"maven-releases\", \"startDate\": \"2016-01-01\" }"
#echo $runScriptRespone #| jq-osx-amd64 -c -r  '.result' | jq-osx-amd64 -c -r  '.assets' | jq-osx-amd64 '.[]'

#runScript countcomponents
#echo $runScriptRespone | jq-osx-amd64 -c -r  '.result' | jq-osx-amd64 '.'

echo "#############################################################################"
printf "Deleting Scripts on $host\n"
echo "#############################################################################"
#deleteScript npmBower
#deleteScript raw
#deleteScript security
#deleteScript core

#deleteScript createmavenrepo
#deleteScript provision
#deleteScript jsontest
#deleteScript listrepos
#deleteScript repoassetlister
#deleteScript countcomponents
#deleteScript listusers


#listCurrentScripts

printf "\nProvisioning Scripts Completed\n\n"

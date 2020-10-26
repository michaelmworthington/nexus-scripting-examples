#!/bin/bash

#curl_verbose="-v"
curl_verbose=""

baseurl="http://localhost:8083/nexus"
nx_creds="admin:admin123"

#placeholder variable to store the script result so i can parse it outside the runScript function with jq
runScriptRespone=""

function list_current_scripts()
{
	echo "#############################################################################"
	printf "Listing Integration API Scripts\n"
	echo "#############################################################################"

	curl $curl_verbose -u $nx_creds "$baseurl/service/rest/v1/script"
	echo ""
	echo ""
	echo ""
}

function run_script()
{
	name=$1
	script_args=$2

	echo "#############################################################################"
	printf "Running Integration API Script $name\n"
	echo "curl $curl_verbose -X POST -u $nx_creds --header 'Content-Type: text/plain' $baseurl/service/rest/v1/script/$name/run -d $script_args"
	echo "#############################################################################"

	if [ -n "$script_args" ]
	then
		runScriptRespone=`curl $curl_verbose -X POST -u $nx_creds --header "Content-Type: text/plain" "$baseurl/service/rest/v1/script/$name/run" -d "$script_args"  2> /dev/null`
	else
		runScriptRespone=`curl $curl_verbose -X POST -u $nx_creds --header "Content-Type: text/plain" "$baseurl/service/rest/v1/script/$name/run" 2> /dev/null`
	fi

	echo ""
	echo ""
	echo ""
}

function setAnonymous()
{
	anonymous=$1

	echo "#############################################################################"
	printf "Setting Anonymous access to $anonymous\n"
	echo "#############################################################################"

	run_script anonymous "-d $anonymous"

}

function add_new_script_to_nexus()
{
	jsonFile=$1

	echo "#############################################################################"
	printf "Creating Integration API Script from $jsonFile\n"
	echo "#############################################################################"

	#the script has to be in json format
	#use the provision.sh for ease of translation from groovy to the script json
	curl $curl_verbose -u $nx_creds --header "Content-Type: application/json" "$baseurl/service/rest/v1/script/" -d @$jsonFile
	echo ""
	echo ""
	echo ""
}

function update_script_in_nexus()
{
	name=$1
	jsonFile=$2

	echo "#############################################################################"
	printf "Updating Integration API Script $name with $jsonFile\n"
	echo "#############################################################################"

	curl $curl_verbose -X PUT -u $nx_creds --header "Content-Type: application/json" "$baseurl/service/rest/v1/script/$name" -d @$jsonFile
	echo ""
	echo ""
	echo ""
}

function delete_script_from_nexus()
{
	name=$1

	echo "#############################################################################"
	printf "Deleting Integration API Script $name\n"
	echo "#############################################################################"

	curl $curl_verbose -X DELETE -u $nx_creds  "$baseurl/service/rest/v1/script/$name"
	echo ""
	echo ""
	echo ""

}


list_current_scripts

#the script can only be added once

#delete_script_from_nexus listrepos
#add_new_script_to_nexus ../simple-shell-example/listrepos.json
#add_new_script_to_nexus ./src/main/groovy/listRepos.groovy

#delete_script_from_nexus anonymous
#add_new_script_to_nexus ../simple-shell-example/anonymous.json

#add_new_script_to_nexus ../simple-shell-example/jsontest.json

#delete_script_from_nexus repoassetlister
#add_new_script_to_nexus ../simple-shell-example/repoAssetLister.json

#add_new_script_to_nexus ./echo.json

#list_current_scripts

#run_script anonymous #sets anonymous to false, default when no values passed in
#setAnonymous true    #sets anonymous back to true

#run_script listrepos
#run_script listrepos | jq-osx-amd64 -c -r  '.result'

#run_script repoassetlister "{ \"repoName\": \"maven-releases\", \"startDate\": \"2016-01-01\" }" #| jq-osx-amd64 -c -r  '.result' | jq-osx-amd64 '.[]'


#!/bin/bash

set -e
[ -z "$DEBUG" ] || set -x;

# SYNTAX:
#        $ ./auto-release.sh <repo> <tag> <token> <last tag>
# EXAMPLE:
#        $ ./auto-release.sh WeBankFinTech/WeIdentity v1.5.0 <token> v1.4.1
#
# Prerequisites:
# 1. Fill in signing and OSS account info in gradle.properties
# 2. pip install gitchangelog
# 3. Finish your CHANGELOG.md and also FINISHED MERGING the release to master (e.g. release/1.5.1 to master)
#
# The script will do the following:
# 1. gradle build (w/o tests)
# 2. create tag and release based on a simple version of CHANGELOG from the current branch HEAD
# 3. upload /dist/app/*.jar to release assets
# 4. upload Archives to OSS Sonatype Central (you must fill in signing info in gradle.properties with private key file)
# 
# Note: Release note is NOT changelog. We strongly suggest to manually write up changelog instead of using gitchangelog as change log.


COMMIT=$(git rev-parse HEAD)

REPO="$1"
shift

TAG="$1"
shift

TOKEN="$1"
shift

LASTTAG="$1"
shift

rm -rf CHANGELOG_simple.md
gitchangelog ^"$LASTTAG" HEAD > temp_CHANGELOG.md
# Convert all line breaks to explicit escape
sed -E ':a;N;$!ba;s/\r{0,1}\n/\\n/g' temp_CHANGELOG.md > temp_CHANGELOG_simple.md
rm -rf temp_CHANGELOG.md
# Remove all asterisks and slash
sed 's/\*\+/\\n/g' temp_CHANGELOG_simple.md > temp_CHANGELOG.md
sed 's/-/\\n/g' temp_CHANGELOG.md > CHANGELOG_simple.md
rm -rf temp_CHANGELOG.md
rm -rf temp_CHANGELOG_simple.md
vi CHANGELOG_simple.md

rm -rf VERSION
touch VERSION
echo "$TAG" > VERSION
sed -i 's/v//g' VERSION

rm -rf dist/app
gradle build -x test

.one-button-release/release.sh $REPO $COMMIT $TAG $TOKEN -- dist/app/*.jar < CHANGELOG_simple.md
rm -rf CHANGELOG_simple.md

# gradle uploadArchives
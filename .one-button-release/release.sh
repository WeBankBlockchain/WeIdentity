#!/bin/bash

set -e
[ -z "$DEBUG" ] || set -x;

usage() {
  echo "$0 <repo> <commit/branch> <tag> <token> [-- <assets>] [< <release notes>]" >&2;
}

if [ "$1" = "-h" -o "$1" = "--help" ]; then
  usage
  cat >&2 <<EOS
Pass the following arguments:
        * \`<repo>\`: ":user/:name" of the repository. For example, "chaoxinhu/WeIdentity".
        * \`<commit/branch>\`: Commit hash or HEAD of your branch name. For example, "master".
        * \`<tag>\`: Name of the tag and the version for this release. For example, "1.7.0".
        * \`<token>\`: GitHub token (permissions required for pushing on your repo).
        * \`-- <assets>\`: Specific standalone assets to be uploaded along with the release.
        * \`< <release notes>\`: Release Notes file (preferably .md).
EXAMPLES:
        $ ./release.sh chaoxinhu/WeIdentity v1.5.0 <token> -- dist/app/*.jar
        Creates a release named "v1.5.0" and adds any jar file in
        \`dist/app/\` as an asset.
        $ ./release.sh chaoxinhu/WeIdentity v1.5.1 <token> -- dist/app/*.jar < notes.md
        Same as above, but also append the paragraph content in notes.md 
        as the release notes.
NOTES:
1. The release notes function is still experimental. Use \n when necessary if you
encounter json parsing errors, in your .md file.
2. Generate GitHub token at https://github.com/settings/tokens and make sure
it has access to the \`"repo"\` scope.
EOS
  exit 1;
fi

[ -n "$2" ] || (usage; exit 1);

REPO="$1"
shift

COMMIT="$1"
shift

TAG="$1"
shift

TOKEN="$1"
shift

if [ "$1" = "--" -a "$#" -ge "2" ]; then
  shift
  ASSETS="$@"
fi

BODY=""
[ -t 0 ] || BODY=`cat`;

API_JSON=$(printf '{"tag_name": "%s", "target_commitish": "%s", "name": "WeIdentity Java SDK %s Release", "body": "Version %s: %s", "draft": false, "prerelease": false}' $TAG "$COMMIT" $TAG $TAG "$BODY")

echo "$API_JSON"

RESP=$(curl --data "$API_JSON" https://api.github.com/repos/$REPO/releases?access_token=$TOKEN)

echo $RESP

upload_url="$(echo "$RESP" | jq -r .upload_url | sed -e "s/{?name,label}//")"

for file in $ASSETS; do
  curl --header "Content-Type:application/gzip" \
       -H "Authorization: token $TOKEN" \
       --data-binary "@$file" \
       "$upload_url?name=$(basename "$file")"
done

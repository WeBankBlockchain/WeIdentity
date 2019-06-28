#!/usr/bin/env sh

if [ "$TRAVIS_BRANCH" = "master" ] && [ "$TRAVIS_PULL_REQUEST" = "false" ];
then
    openssl aes-256-cbc -K $encrypted_617a57e4a121_key -iv $encrypted_617a57e4a121_iv -in secring.gpg.enc -out secring.gpg -d
fi

#!/bin/bash
# AppEngine SDK cannot compile jsp files properly under Java 8, so need to force Java 7 :-(
PATH=/usr/lib/jvm/java-7-oracle/bin:$PATH ../appengine-java-sdk*/bin/appcfg.sh --oauth2 update out/artifacts/picasa_gallery_war_exploded/

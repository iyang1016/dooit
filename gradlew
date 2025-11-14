#!/usr/bin/env sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# @author Gunnar Hillert
#

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass any JVM options to Gradle and arguments to Gradle respectively.
DEFAULT_JVM_OPTS=""

APP_HOME_DIR="$(cd "$(dirname "$0")" && pwd -P)"
APP_NAME="Gradle"
APP_BASE_NAME="$(basename "$0")"

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
msys=false
darwin=false
nonstop=false
case "$(uname)" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

# For Cygwin, ensure paths are in UNIX format before anything is touched.
if ${cygwin} ; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
  [ -n "$GRADLE_HOME" ] && GRADLE_HOME="$(cygpath --unix "$GRADLE_HOME")"
fi

# Attempt to set APP_HOME
if [ -z "$APP_HOME" ] ; then
  ## resolve links - $0 may be a link to gradle's home
  PRG="$0"
  # need this for relative symlinks
  while [ -h "$PRG" ] ; do
    ls="$(ls -ld "$PRG")"
    link="$(expr "$ls" : '.*-> \(.*\)$')"
    if expr "$link" : '/.*' > /dev/null; then
      PRG="$link"
    else
      PRG="$(dirname "$PRG")/$link"
    fi
  done
  APP_HOME="$(dirname "$PRG")"
fi

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
  if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
    # IBM's JDK on AIX uses strange locations for the executables
    JAVACMD="$JAVA_HOME/jre/sh/java"
  else
    JAVACMD="$JAVA_HOME/bin/java"
  fi
  if [ ! -x "$JAVACMD" ] ; then
    die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
  fi
else
  JAVACMD="java"
  which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if ! ${cygwin} && ! ${darwin} && ! ${nonstop} ; then
  if [ "$MAX_FD" = "maximum" ] || [ "$MAX_FD" = "max" ] ; then
    # Use the maximum available limit.
    # Surely this is not equal to the soft limit, so it should indicate that the limit was changed.
    soft_max_fd="$(ulimit -Sn)"
    if [ "$?" = "0" ] ; then
      hard_max_fd="$(ulimit -Hn)"
      if [ "$?" = "0" ] ; then
        if [ "$soft_max_fd" != "$hard_max_fd" ] ; then
          ulimit -n "$hard_max_fd"
          if [ "$?" != "0" ] ; then
            # If we can't set the ulimit, we can't do anything, so ignore the error.
            true
          fi
        fi
      fi
    fi
  else
    ulimit -n "$MAX_FD"
    if [ "$?" != "0" ] ; then
      # If we can't set the ulimit, we can't do anything, so ignore the error.
      true
    fi
  fi
fi

# Add the jar to the classpath
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# Split up the JVM options only if the JVM is not IBM's.
# Does not apply to nonstop.
if [ -z "${IBM_JAVA_COMMAND}" ] && [ "${nonstop}" = "false" ] ; then
  # Filter out duplicate VM options and memory settings.
  # Using awk, because it's the most portable option.
  # See https://github.com/gradle/gradle/issues/12837.
  DEFAULT_JVM_OPTS="$(echo "${DEFAULT_JVM_OPTS}" | awk '
    {
      for (i=1; i<=NF; i++) {
        if ( ($i ~ /^-Xm[sx]/) || ($i ~ /^-XX:MaxPermSize/) || ($i ~ /^-XX:MaxMetaspaceSize/) || ($i ~ /^--add-opens/) ) {
          if (index(jvmmem, $i) == 0) {
            jvmmem = jvmmem $i " "
          }
        } else if (index(jvmargs, $i) == 0) {
          jvmargs = jvmargs $i " "
        }
      }
      print jvmargs jvmmem
    }
  ')"
fi

# Collect all arguments for the java command, following the shell quoting rules.
# See https://github.com/gradle/gradle/issues/15343
eval set -- "${DEFAULT_JVM_OPTS}" "${JAVA_OPTS:--Dorg.gradle.appname=${APP_BASE_NAME}}" -classpath "\"${CLASSPATH}\"" org.gradle.wrapper.GradleWrapperMain "${GRADLE_OPTS}" '"$@"'

# Stop on error
set -e

# Kick it off
exec "${JAVACMD}" "$@"

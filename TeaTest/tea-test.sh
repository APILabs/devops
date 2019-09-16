#!/bin/bash

# NOTE: This script is merely an example to show how the pipeline
#       works in various stages; actual testing would use other
#       technologies (python, junit, etc).

# This script assumes an existing server on port 7800
rc=0
curl -i http://localhost:7800/tea/v1/index/0 > /tmp/curl.out 2>/dev/null
grep -q "200 OK" /tmp/curl.out
if [ "$?" != "0" ]; then
    echo "Failed to get tea index 0"
    cat /tmp/curl.out
    rc=1
fi

exit $((rc))



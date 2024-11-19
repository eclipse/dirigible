#!/bin/bash

# URLs to call
URLS=(
    "http://localhost:8080/telemetry/otel-metric-counter"
    "http://localhost:8080/telemetry/micrometer-counter"
    "http://localhost:8080/telemetry/otel-span?data=some-client-data"
    "http://localhost:8080/telemetry/otel-span_failure"
    "http://localhost:8080/telemetry/logs"
)

# Infinite loop
while true; do
    for URL in "${URLS[@]}"; do
        echo '----------------------------------------------------------------------------------------------------'
        # Randomly decide whether to skip this URL
        SKIP=$((RANDOM % 2)) # 50% chance to skip
        if [[ $SKIP -eq 1 ]]; then
            echo "Skipping $URL"
            continue
        fi

        # Make the HTTP GET request
        echo "Calling $URL"
        curl -s -o /dev/null -w "%{http_code}\n" "$URL"

        # Random sleep time in milliseconds (0 to 1000 ms)
        SLEEP_MS=$((RANDOM % 1001))
        echo "Sleeping for $SLEEP_MS milliseconds"

        # Convert to seconds and sleep
        sleep "$(awk "BEGIN {print $SLEEP_MS / 1000}")"

    done
done

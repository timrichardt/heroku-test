#!/bin/bash

RESULT_VALID_INPUT=$(curl -X POST https://morning-inlet-68068.herokuapp.com/ \
     -H "Content-Type: application/json;charset=utf-8" \
     --data-binary @- <<EOF
{
    "address": {
        "colorKeys": [
            "A",
            "G",
            "Z"
        ],
        "values": [
            74,
            117,
            115,
            116,
            79,
            110
        ]     
    },
    "meta": {
        "digits": 33,
        "processingPattern": "d{5}+[a-z&$§]"
    }
}								 
EOF
					 )

RESULT_INVALID_VALUES=$(curl -X POST https://morning-inlet-68068.herokuapp.com/ \
     -H "Content-Type: application/json;charset=utf-8" \
     --data-binary @- <<EOF
{
    "address": {
        "colorKeys": [
            "A",
            "G",
            "Z"
        ],
        "values": [
            74,
            117,
            115,
            116,
            79,
            "110"
        ]     
    },
    "meta": {
        "digits": 33,
        "processingPattern": "d{5}+[a-z&$§]"
    }
}								 
EOF
					 )

RESULT_MISSING_KEY=$(curl -X POST https://morning-inlet-68068.herokuapp.com/ \
     -H "Content-Type: application/json;charset=utf-8" \
     -d "{}")


if [[ $RESULT_VALID_INPUT == "{\"result\":8}" ]]; then
		echo "✓ Check result for valid input"
else
		echo "✗ Check result for valid input failed:"
		echo $RESULT_VALID_INPUT
		exit 1
fi
if [[ $RESULT_INVALID_VALUES == "{\"error\":\"Invalid request input: address.input must be an array of integers.\",\"address.values\":[74,117,115,116,79,\"110\"]}" ]]; then
		echo "✓ Check error for invalid values input"
else
		echo "✗ Check error for invalid values input failed:"
		echo $RESULT_INVALID_VALUES
		exit 1
fi
		
if [[ $RESULT_MISSING_KEY == "{\"error\":\"Invalid request input: no key address.values.\"}" ]]; then
		echo "✓ Check error for missing key in input"
else
		echo "✗ Check error for missing key in input failed:"
		echo $RESULT_MISSING_KEY
		exit 1
fi

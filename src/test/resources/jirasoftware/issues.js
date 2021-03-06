{
    "expand": "names,schema",
    "startAt": 0,
    "maxResults": 50,
    "total": 2,
    "issues": [
        {
            "expand": "",
            "id": "10001",
            "self": "http://www.example.com/jira/rest/agile/1.0/board/92/issue/10001",
            "key": "HSP-1",
            "fields": {
                "flagged": true,
                "sprint": {
                    "id": 37,
                    "self": "http://www.example.com/jira/rest/agile/1.0/sprint/13",
                    "state": "future",
                    "name": "sprint 2"
                },
                "closedSprints": [
                    {
                        "id": 37,
                        "self": "http://www.example.com/jira/rest/agile/1.0/sprint/23",
                        "state": "closed",
                        "name": "sprint 1",
                        "startDate": "2015-04-11T15:22:00.000+10:00",
                        "endDate": "2015-04-20T01:22:00.000+10:00",
                        "completeDate": "2015-04-20T11:04:00.000+10:00"
                    }
                ],
                "description": "example bug report",
                "project": {
                    "self": "http://www.example.com/jira/rest/api/2/project/EX",
                    "id": "10000",
                    "key": "EX",
                    "name": "Example",
                    "avatarUrls": {
                        "48x48": "http://www.example.com/jira/secure/projectavatar?size=large&pid=10000",
                        "24x24": "http://www.example.com/jira/secure/projectavatar?size=small&pid=10000",
                        "16x16": "http://www.example.com/jira/secure/projectavatar?size=xsmall&pid=10000",
                        "32x32": "http://www.example.com/jira/secure/projectavatar?size=medium&pid=10000"
                    },
                    "projectCategory": {
                        "self": "http://www.example.com/jira/rest/api/2/projectCategory/10000",
                        "id": "10000",
                        "name": "FIRST",
                        "description": "First Project Category"
                    }
                },
                "comment": [
                    {
                        "self": "http://www.example.com/jira/rest/api/2/issue/10010/comment/10000",
                        "id": "10000",
                        "author": {
                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
                            "name": "fred",
                            "displayName": "Fred F. User",
                            "active": false
                        },
                        "body": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque eget venenatis elit. Duis eu justo eget augue iaculis fermentum. Sed semper quam laoreet nisi egestas at posuere augue semper.",
                        "updateAuthor": {
                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
                            "name": "fred",
                            "displayName": "Fred F. User",
                            "active": false
                        },
                        "created": "2017-02-08T17:08:41.328+0000",
                        "updated": "2017-02-08T17:08:41.330+0000",
                        "visibility": {
                            "type": "role",
                            "value": "Administrators"
                        }
                    }
                ],
                "epic": {
                    "id": 37,
                    "self": "http://www.example.com/jira/rest/agile/1.0/epic/23",
                    "name": "epic 1",
                    "summary": "epic 1 summary",
                    "color": {
                        "key": "color_4"
                    },
                    "done": true
                },
                "worklog": [
                    {
                        "self": "http://www.example.com/jira/rest/api/2/issue/10010/worklog/10000",
                        "author": {
                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
                            "name": "fred",
                            "displayName": "Fred F. User",
                            "active": false
                        },
                        "updateAuthor": {
                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
                            "name": "fred",
                            "displayName": "Fred F. User",
                            "active": false
                        },
                        "comment": "I did some work here.",
                        "updated": "2017-02-08T17:08:41.332+0000",
                        "visibility": {
                            "type": "group",
                            "value": "jira-developers"
                        },
                        "started": "2017-02-08T17:08:41.332+0000",
                        "timeSpent": "3h 20m",
                        "timeSpentSeconds": 12000,
                        "id": "100028",
                        "issueId": "10002"
                    }
                ],
                "updated": 1,
                "timetracking": {
                    "originalEstimate": "10m",
                    "remainingEstimate": "3m",
                    "timeSpent": "6m",
                    "originalEstimateSeconds": 600,
                    "remainingEstimateSeconds": 200,
                    "timeSpentSeconds": 400
                }
            }
        },
        {
            "expand": "",
            "id": "10001",
            "self": "http://www.example.com/jira/rest/agile/1.0/board/92/issue/10001",
            "key": "HSP-1",
            "fields": {
                "flagged": true,
                "sprint": {
                    "id": 37,
                    "self": "http://www.example.com/jira/rest/agile/1.0/sprint/13",
                    "state": "future",
                    "name": "sprint 2"
                },
                "closedSprints": [
                    {
                        "id": 37,
                        "self": "http://www.example.com/jira/rest/agile/1.0/sprint/23",
                        "state": "closed",
                        "name": "sprint 1",
                        "startDate": "2015-04-11T15:22:00.000+10:00",
                        "endDate": "2015-04-20T01:22:00.000+10:00",
                        "completeDate": "2015-04-20T11:04:00.000+10:00"
                    }
                ],
                "description": "example bug report",
                "project": {
                    "self": "http://www.example.com/jira/rest/api/2/project/EX",
                    "id": "10000",
                    "key": "EX",
                    "name": "Example",
                    "avatarUrls": {
                        "48x48": "http://www.example.com/jira/secure/projectavatar?size=large&pid=10000",
                        "24x24": "http://www.example.com/jira/secure/projectavatar?size=small&pid=10000",
                        "16x16": "http://www.example.com/jira/secure/projectavatar?size=xsmall&pid=10000",
                        "32x32": "http://www.example.com/jira/secure/projectavatar?size=medium&pid=10000"
                    },
                    "projectCategory": {
                        "self": "http://www.example.com/jira/rest/api/2/projectCategory/10000",
                        "id": "10000",
                        "name": "FIRST",
                        "description": "First Project Category"
                    }
                },
                "comment": [
                    {
                        "self": "http://www.example.com/jira/rest/api/2/issue/10010/comment/10000",
                        "id": "10000",
                        "author": {
                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
                            "name": "fred",
                            "displayName": "Fred F. User",
                            "active": false
                        },
                        "body": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque eget venenatis elit. Duis eu justo eget augue iaculis fermentum. Sed semper quam laoreet nisi egestas at posuere augue semper.",
                        "updateAuthor": {
                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
                            "name": "fred",
                            "displayName": "Fred F. User",
                            "active": false
                        },
                        "created": "2017-02-08T17:08:41.328+0000",
                        "updated": "2017-02-08T17:08:41.330+0000",
                        "visibility": {
                            "type": "role",
                            "value": "Administrators"
                        }
                    }
                ],
                "epic": {
                    "id": 37,
                    "self": "http://www.example.com/jira/rest/agile/1.0/epic/23",
                    "name": "epic 1",
                    "summary": "epic 1 summary",
                    "color": {
                        "key": "color_4"
                    },
                    "done": true
                },
                "worklog": [
                    {
                        "self": "http://www.example.com/jira/rest/api/2/issue/10010/worklog/10000",
                        "author": {
                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
                            "name": "fred",
                            "displayName": "Fred F. User",
                            "active": false
                        },
                        "updateAuthor": {
                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
                            "name": "fred",
                            "displayName": "Fred F. User",
                            "active": false
                        },
                        "comment": "I did some work here.",
                        "updated": "2017-02-08T17:08:41.332+0000",
                        "visibility": {
                            "type": "group",
                            "value": "jira-developers"
                        },
                        "started": "2017-02-08T17:08:41.332+0000",
                        "timeSpent": "3h 20m",
                        "timeSpentSeconds": 12000,
                        "id": "100028",
                        "issueId": "10002"
                    }
                ],
                "updated": 1,
                "timetracking": {
                    "originalEstimate": "10m",
                    "remainingEstimate": "3m",
                    "timeSpent": "6m",
                    "originalEstimateSeconds": 600,
                    "remainingEstimateSeconds": 200,
                    "timeSpentSeconds": 400
                }
            }
        }
    ]
}
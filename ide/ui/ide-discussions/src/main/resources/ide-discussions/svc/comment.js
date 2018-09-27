"use strict";

var comments = require("ide-discussions/lib/comments_service_lib").create();
comments.mappings().readonly();
comments.execute();

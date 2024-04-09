

import { rs } from "sdk/http";

rs.service()
    .resource("")
        .get(function(ctx, request, response){
            response.println("Hello there!");
        })
.execute();
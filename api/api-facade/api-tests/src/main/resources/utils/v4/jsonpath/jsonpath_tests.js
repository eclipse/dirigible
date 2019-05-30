/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var logging = require('log/v4/logging');
var logger = logging.getLogger('org.eclipse.dirigible.jsonpath.tests');	
var JSONPath = require('utils/v4/jsonpath');

var testSuiteState;

var json = {
    "store": {
        "book": [
            {
                "category": "reference",
                "author": "Nigel Rees",
                "title": "Sayings of the Century",
                "price": 8.95
            },
            {
                "category": "fiction",
                "author": "Evelyn Waugh",
                "title": "Sword of Honour",
                "price": 12.99
            },
            {
                "category": "fiction",
                "author": "Herman Melville",
                "title": "Moby Dick",
                "isbn": "0-553-21311-3",
                "price": 8.99
            },
            {
                "category": "fiction",
                "author": "J. R. R. Tolkien",
                "title": "The Lord of the Rings",
                "isbn": "0-395-19395-8",
                "price": 22.99
            }
        ],
        "bicycle": {
            "color": "red",
            "price": 19.95
        }
    }
};

// TESTS (Examples)

[{
    path: 'store.book[*].author',
    desc: 'The authors of all books in the store',
    itemsNumber: 4,
    assertion: function(jps, actual){
        var expected = json.store.book.map(function(b){
            return b.author;
        });
        if(!java.util.Arrays["equals(java.lang.Object[],java.lang.Object[])"](actual, expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$..author',
    desc: 'All authors',
    itemsNumber: 4,
    assertion: function(jps, actual){
        var expected = json.store.book.map(function(b){
            return b.author;
        });
        if(!java.util.Arrays["equals(java.lang.Object[],java.lang.Object[])"](actual, expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: 'store.*',
    desc: 'All things in store, which are its books (a book array) and a red bicycle (a bicycle object).',
    itemsNumber: 2,
    assertion: function(jps, actual){
        var expected = [json.store.book, json.store.bicycle];
        if(!java.util.Arrays["equals(java.lang.Object[],java.lang.Object[])"](actual, expected)){
            throw Error('expected `'+JSON.stringify(expected,null,2)+'` but was `'+JSON.stringify(actual,null,2)+'`');
        }
    }
},{
    path: 'store..price',
    desc: 'The price of everything in the store.',
    itemsNumber: 5,
    assertion: function(jps, actual){
        var expected = json.store.book.map(function(b){
            return b.price;
        }).concat(json.store.bicycle.price);
        if(!java.util.Arrays["equals(java.lang.Object[],java.lang.Object[])"](actual, expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$..book[2]',
    desc: 'The third book (book object)',
    itemsNumber: 1,
    assertion: function(jps, actual){
        var expected = [json.store.book[2]];
        if(JSON.stringify(actual) !== JSON.stringify(expected) ){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$..book[(@.length-1)]',
    desc: 'The last book in order.',
    itemsNumber: 1,
    assertion: function(jps, actual){
        var expected = [json.store.book[json.store.book.length-1]];
        if(JSON.stringify(actual) !== JSON.stringify(expected) ){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$..book[-1:]',
    desc: 'The last book in order.',
    itemsNumber: 1,
    assertion: function(jps, actual){
        var expected = [json.store.book[json.store.book.length-1]];
        if(JSON.stringify(actual) !== JSON.stringify(expected) ){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$..book[0,1]',
    desc: 'The first two books.',
    itemsNumber: 2,
    assertion: function(jps, actual){
        var expected = json.store.book.slice(0, 2);
        if(!java.util.Arrays["equals(java.lang.Object[],java.lang.Object[])"](actual, expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$..book[:2]',
    desc: 'The first two books.',
    itemsNumber: 2,
    assertion: function(jps, actual){
        // Note: Buggy
        // actual is `[{ "category": "reference", "author": "Nigel Rees", "title": "Sayings of the Century", "price": 8.95 }, { "category": "reference", "author": "Nigel Rees", "title": "Sayings of the Century", "price": 8.95 }]`,
        // which is wrong.
        // The expected result is `[{"category":"reference","author":"Nigel Rees","title":"Sayings of the Century","price":8.95},{"category":"fiction","author":"Evelyn Waugh","title":"Sword of Honour","price":12.99}]` 
        var expected = json.store.book.slice(0, 2);
        if(!java.util.Arrays["equals(java.lang.Object[],java.lang.Object[])"](actual, expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$..book[0][category,author]',
    desc: 'The categories and authors of all books.',
    itemsNumber: 2,
    assertion: function(jps, actual){
        var expected = [json.store.book[0].category,json.store.book[0].author];
        if(!java.util.Arrays["equals(java.lang.Object[],java.lang.Object[])"](actual, expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$..book[?(@.isbn)]',
    desc: 'Filter all books with an ISBN number.',
    itemsNumber: 2,
    assertion: function(jps, actual){
        var expected = json.store.book.filter(function(b){
            return b.isbn !== undefined;
        });
        if(!java.util.Arrays["equals(java.lang.Object[],java.lang.Object[])"](actual, expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$..book[?(@.price<10)]',
    desc: 'Filter all books cheaper than 10.',
    itemsNumber: 2,
    assertion: function(jps, actual){
        var expected = json.store.book.filter(function(b){
            return b.price < 10;
        });
        if(!java.util.Arrays["equals(java.lang.Object[],java.lang.Object[])"](actual, expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: "$..*[?(@property === 'price' && @ !== 8.95)]",
    desc: 'Obtain all property values of objects whose property is price and which does not equal 8.95.',
    itemsNumber: 4,
    assertion: function(jps, actual){
        var expected = [19.95,12.99,8.99,22.99];
        if(!java.util.Arrays["equals(java.lang.Object[],java.lang.Object[])"](actual, expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$',
    desc: 'The root of the JSON object (i.e., the whole object itself).',
    itemsNumber: 1,
    assertion: function(jps, actual){
        var expected =[json];
        if(JSON.stringify(actual)!==JSON.stringify(expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$..*',
    desc: 'All members of a JSON structure beneath the root.',
    itemsNumber: 27,
    assertion: function(jps, actual){
        var expected =[json.store, json.store.book].concat(json.store.bicycle).concat(json.store.book);
        for(var i=0; i<json.store.book.length; i++){
            for (var j in json.store.book[i]){
                expected = expected.concat(json.store.book[i][j]);
            }
        }
        for (var i in json.store.bicycle){
            expected = expected.concat(json.store.bicycle[i]);
        }
        if(!java.util.Arrays["equals(java.lang.Object[],java.lang.Object[])"](actual, expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$..',
    desc: 'All parent components of a JSON structure including root.',
    itemsNumber: 8,
    assertion: function(jps, actual){
        var expected =[json, json.store, json.store.book].concat(json.store.book).concat(json.store.bicycle);
        if(!java.util.Arrays["equals(java.lang.Object[],java.lang.Object[])"](actual, expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$..[?(@.price>19)]^',
    desc: 'Parent of those specific items with a price greater than 19 (i.e., the store value as the parent of the bicycle and the book array as parent of an individual book)',
    itemsNumber: 2,
    assertion: function(jps, actual){
        var expected = [json.store, json.store.book];
        if(JSON.stringify(actual)!==JSON.stringify(expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$.store.*~',
    desc: 'The property names of the store sub-object ("book" and "bicycle"). Useful with wildcard properties.',
    itemsNumber: 2,
    assertion: function(jps, actual){
        var expected = ["book", "bicycle"];
        if(JSON.stringify(actual)!==JSON.stringify(expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$.store.book[?(@path !== "$[\'store\'][\'book\'][0]")]',
    desc: 'All books besides that at the path pointing to the first',
    itemsNumber: 3,
    assertion: function(jps, actual){
        var expected = json.store.book.slice(1);
        if(JSON.stringify(actual)!==JSON.stringify(expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$..book[?(@parent.bicycle && @parent.bicycle.color === "red")].category',
    desc: 'Grabs all categories of books where the parent object of the book has a bicycle child whose color is red (i.e., all the books)',
    itemsNumber: 4,
    assertion: function(jps, actual){
        var expected = json.store.book.map(function(b){
            return b.category;
        });
        if(JSON.stringify(actual)!==JSON.stringify(expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$..book[?(@property !== 0)]',
    desc: 'Grabs all children of "book" except for "category" ones',
    itemsNumber: 3,
    assertion: function(jps, actual){
        var expected = json.store.book.slice(1);
        if(JSON.stringify(actual)!==JSON.stringify(expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$.store.*[?(@parentProperty !== "book")]',
    desc: "Grabs the grandchildren of store whose parent property is not book (i.e., bicycle's children, 'color' and 'price')",
    itemsNumber: 2,
    assertion: function(jps, actual){
        var expected = [json.store.bicycle.color, json.store.bicycle.price];
        if(JSON.stringify(actual)!==JSON.stringify(expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$..book.*[?(@parentProperty !== 0)]',
    desc: 'Get the property values of all book instances whereby the parent property of these values (i.e., the array index holding the book item parent object) is not 0',
    itemsNumber: 14,
    assertion: function(jps, actual){
        var expected = [];
		var b = json.store.book.slice(1);
		for (var i=0; i<b.length; i++){
			for(var j in b[i]){
				expected.push(b[i][j]);
			}
		}
        if(JSON.stringify(actual)!==JSON.stringify(expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
},{
    path: '$..book..*@number()',
    desc: 'Get the numeric values within the book array',
    itemsNumber: 4,
    assertion: function(jps, actual){
        var expected = json.store.book.map(function(b){
            return b.price;
        });
        if(JSON.stringify(actual)!==JSON.stringify(expected)){
            throw Error('expected `'+JSON.stringify(expected)+'` but was `'+JSON.stringify(actual)+'`');
        }
    }
}].forEach(function(tc){
    var actual = JSONPath({
        "path": tc.path, 
        "json": json
    });
    logger.debug("{} {}", tc.path, JSON.stringify(actual,null,2))
    try{
        if (!Array.isArray(actual)){
            throw Error("expected to return array, but was" + (typeof actual))
        }
        if (actual.length != tc.itemsNumber){
            throw Error("expected to return "+tc.itemsNumber+" items, but they were " + actual.length)
        }
        tc.assertion(tc.path, actual);
        logger.info("[Pass] {}", tc.path)
    } catch (tcerr){
        logger.error("[Fail] {}: {}", tc.path, tcerr.message);
    }
});

testSuiteState == true;
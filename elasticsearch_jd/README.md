学习elasticsearch
GET /test1

PUT /t_est2ffd

PUT /test2
{
  "aliases": {
    "alias_1": {},
    "alias_2": {
      "filter": {
        "term": { "user.id": "kimchy" }
      },
      "routing": "shard-1"
    }
  }
}

GET /test2

#获取别名
GET /test2/_alias

GET /shard-1

DELETE /test2

GET /test3

PUT /test3?pretty
{
  "aliases": {
    "alias_1": {},
    "alias_2": {
      "filter": {
        "term": { "user.id": "kimchy" }
      },
      "routing": "shard-1"
    }
  }
}

PUT /publications?pretty
{
  "mappings": {
    "properties": {
      "id": { "type": "text" },
      "title": { "type": "text" },
      "abstract": { "type": "text" },
      "author": {
        "properties": {
          "id": { "type": "text" },
          "name": { "type": "text" }
        }
      }
    }
  }
}


GET /publications/_mapping/field/title?pretty

GET / _xpack

POST /my-index-000001/_doc?routing=kimchy&pretty
{
  "@timestamp": "2099-11-15T13:12:00",
  "message": "GET /search HTTP/1.1 200 1070000",
  "user": {
    "id": "kimchy"
  }

}

GET /test1

PUT /test1/test/1 
{
  "name": "alex",
  "age": "18"
}

PUT /test1/test/2
{
  "name": "marry",
  "age": "18"
}

GET /test1/test/2

POST /test1/test/
{
  "name": "乔巴",
  "age": 10,
  "dec": "毛绒玩具，宠物，移动粮食库"
}

#update更新时必须在"doc"中
POST /test1/test/1/_update
{
  "doc": {
    "name": "jack"
  }
}

GET /test1/test/1

PUT /test1/test/1
{
  "name": "alex",
  "age": "18"
}

DELETE /test1/test/pn0SK3UBWH8iYosC9ekj


GET /test1/test/1

GET /test1/_search
{
  "query": {
    "match": {
      "name": "c 罗 的"
    }
  }
}


狂神Elasticsearch7.6模拟京东搜索代码实现
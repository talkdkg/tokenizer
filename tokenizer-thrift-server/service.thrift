namespace java org.tokenizer.thrift

enum ThriftGenderType {
    MALE,
    FEMALE
}

struct ThriftMessageRecord {
   1: required string id,
   2: optional string source,
   3: optional string topic,
   4: optional i64 timestamp,
   5: optional string author,
   6: optional byte age,
   7: optional ThriftGenderType gender,
   8: optional string title,
   9: optional string content,
   10: optional string userRating
}

struct ThriftQueryResponse {
	1: optional i64 numFound,
	2: optional i32 qTime,
	3: optional i32 elapsedTime,
	4: optional list<ThriftMessageRecord> messages,
	5: optional string error
}

service ThriftTokenizerService {  
	ThriftQueryResponse get_message_records(1:string query, 2:i32 start, 3:i32 rows),
	ThriftQueryResponse get_message_records_by_date_range(1:string query, 2:i32 start, 3:i32 rows, 4:i64 startTime, 5:i64 endTime),
	ThriftQueryResponse get_message_records_by_date_range_and_source(1:string query, 2:i32 start, 3:i32 rows, 4:i64 startTime, 5:i64 endTime, 6:string source),
	ThriftQueryResponse get_message_records_by_source(1:string query, 2:i32 start, 3:i32 rows, 6:string source)
}


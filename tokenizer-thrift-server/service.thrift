namespace java org.tokenizer.thrift

enum Gender {
    MALE,
    FEMALE
}

struct TokenizerDocument {
   string id,
   string source,
   i64 date,
   string author,
   string age,
   Gender gender,
   string title,
   string content,
   string topic,
   string userRating,
   i32 sentiment,
   set<string> features
}

struct ThriftQueryResponse {
	1: optional i64 numFound,
	2: optional i32 qTime,
	3: optional i32 elapsedTime,
	4: optional list<TokenizerDocument> documents,
	5: optional string error
}

service ThriftTokenizerService {  
	ThriftQueryResponse get_message_records(1:string query, 2:i32 start, 3:i32 rows),
	ThriftQueryResponse get_message_records_by_date_range(1:string query, 2:i32 start, 3:i32 rows, 4:i64 startTime, 5:i64 endTime),
	ThriftQueryResponse get_message_records_by_date_range_and_source(1:string query, 2:i32 start, 3:i32 rows, 4:i64 startTime, 5:i64 endTime, 6:string source),
	ThriftQueryResponse get_message_records_by_source(1:string query, 2:i32 start, 3:i32 rows, 6:string source)
	ThriftQueryResponse retrieve_messages(
		1:required string query, 
		2:required i32 start, 
		3:required i32 rows, 
		4:optional i64 startTime, 
		5:optional i64 endTime, 
		6:optional list<string> sources, 
		7:optional list<string> countryCodes,
		8:optional i32 startAge,
		9:optional i32 endAge,
		10:optional Gender gender,
		11:optional list<string> languageCodes)
}


namespace java com.iflyknow.quality_check.thrift.asr

struct ASRWrite{
	1:i32 epStatus,
	2:i32 rsltStatus,
	3:i32 errorCode,
}
struct ASRResult{
	1:i32 rsltStatus,
	2:i32 errorCode,
	3:string text,
	4:i32 isInterrupted
}
service ASRServ{
	void asr_init(1:string username,2:string password,3:string login_params),
	i32 ASR_create_session(1:string params, 2:string sessionid),
	ASRWrite ASR_audio_write(1:binary text,2:i32 audioStaus),
	ASRResult ASR_get_result(),
	i32 ASR_end_session(),
}

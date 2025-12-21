package com.example.englishapp.data.api;

/**
 * Callback interface cho OpenAI API
 * Xử lý kết quả từ machine learning model sau khi phân tích ngôn ngữ tự nhiên
 */
public interface OpenAICallBack {
    /**
     * Callback khi AI trả lời thành công
     * @param reply Câu trả lời từ AI đã được xử lý bằng machine learning
     */
    void onSuccess (String reply);
    
    /**
     * Callback khi có lỗi xảy ra trong quá trình xử lý
     * @param error Thông báo lỗi
     */
    void onError (String error);
}

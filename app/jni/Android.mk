include $(CLEAR_VARS)
LOCAL_PATH := $(call my-dir)
LOCAL_MODULE := jnetpcap
LOCAL_C_INCLUDE := C:\Users\PC\Documents\Antline\MyInterCepter\app\jni\src\
LOCAL_SRC_FILES := \
                    C:\Users\PC\Documents\Antline\MyInterCepter\app\jni\src\jnetpcap.cpp \
                    jnetpcap_beta.cpp \
                    jnetpcap_bpf.cpp \
                    jnetpcap_dumper.cpp \
                    jnetpcap_ids.cpp \
                    jnetpcap_pcap_header.cpp \
                    jnetpcap_utils.cpp \
                    nio_jbuffer.cpp \
                    nio_jmemory.cpp \
                    nio_jnumber.cpp \
                    packet_flow.cpp \
                    packet_jheader.cpp \
                    packet_jheader_scanner.cpp \
                    packet_jpacket.cpp \
                    packet_jscan.cpp \
                    packet_jsmall_scanner.cpp \
                    packet_protocol.cpp \
                    util_checksum.cpp \
                    util_debug.cpp \
                    util_in_cksum.cpp \
                    winpcap_ext.cpp \
                    winpcap_ids.cpp \
                    winpcap_send_queue.cpp \
                    winpcap_stat_ex.cpp
include $(BUILD_SHARED_LIBRARY)
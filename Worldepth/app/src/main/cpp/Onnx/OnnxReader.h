//
// Created by leodw on 5/27/2019.
//

#ifndef WORLDEPTH_ONNXREADER_H
#define WORLDEPTH_ONNXREADER_H
#include <memory>
#include <string>
#include <flatbuffers/flatbuffers.h>
#include <onnx/onnx_pb.h>

namespace dnn {
    class OnnxReader {
    public:
        void ReadOnnx(const std::string &filepath, ModelBuilder &builder);
        void ReadOnnx(const uint8_t *buf, const size_t size, ModelBuilder &builder);
        void ReadOnnx(const ONNX_NAMESPACE::ModelProto &model_proto, ModelBuilder &builder);
    };
}

#endif //WORLDEPTH_ONNXREADER_H

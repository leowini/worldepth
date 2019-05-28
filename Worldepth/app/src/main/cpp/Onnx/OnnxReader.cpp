//
// Created by leodw on 5/27/2019.
//

#include <sstream>
#include <fstream>
#include "OnnxReader.h"

namespace dnn {
    void OnnxReader::ReadOnnx(const std::string &filepath, ModelBuilder &builder) {
        ONNX_NAMESPACE::ModelProto model_proto;
        {
            std::ifstream ifs(filepath, std::ios::in | std::ios::binary);
            std::stringstream ss;
            ss << ifs.rdbuf();
            // FIXME: Handle the return value
            model_proto.ParseFromString(ss.str());
            ifs.close();
        }
        ReadOnnx(model_proto, builder);
    }

    void OnnxReader::ReadOnnx(const ONNX_NAMESPACE::ModelProto &model_proto, ModelBuilder &builder) {
        OnnxConverter converter;
        converter.Convert(model_proto);
        auto buf = converter.GetBuf();
    }
}
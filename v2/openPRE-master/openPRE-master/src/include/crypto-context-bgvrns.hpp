#include <iostream>

extern "C"{
    void cryptoContextBGVrnsGen(const char* CRYPTOFOLDER,
     const char* filename,
      int plaintextModulus,
       int ringDimension,
        const char* securityLevel);
}
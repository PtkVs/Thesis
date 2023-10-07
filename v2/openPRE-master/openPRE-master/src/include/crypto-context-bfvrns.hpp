#include <iostream>

extern "C"{
    void cryptoContextBFVrnsGen(const char* CRYPTOFOLDER,
     const char* filename,
      int plaintextModulus,
       int ringDimension,
        const char* securityLevel);
}
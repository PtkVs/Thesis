#include <iostream>
extern "C"{
    void cryptoContextGen(const char* schemeName,
     const char* CRYPTOFOLDER,
      const char* filename,
       int plaintextModulus,
        int ringDimension,
         const char* securityLevel);
}
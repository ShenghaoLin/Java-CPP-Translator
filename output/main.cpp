#include "output.h"

using namespace java::lang;

int main(int argc, char* argv[])
{
    // Implement generic interface between C++'s main function and Java's main function
    __rt::Array<String> args = new __rt::__Array<String>(argc - 1);

    for (int32_t i = 1; i < argc; i++)
    {
        (*args)[i] = __rt::literal(argv[i]);
    }

    inputs::test023::__Test023::main(args);

    return 0;
}

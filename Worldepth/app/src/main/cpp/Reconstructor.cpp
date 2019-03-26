#include "Reconstructor.h"

void Reconstructor::reconstruct() {
    Slam();
    Poisson();
    TextureMap();
}
package org.flexdata.nn.initialization;

import java.util.Random;

public interface Distribution {
    void init();

    float next(Random random);
}

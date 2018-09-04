#!/ms/dist/python/PROJ/core/3.4.4/bin/python

import ms.version

ms.version.addpkg('numpy', '1.14.2')
ms.version.addpkg('scipy', '1.0.0')
ms.version.addpkg('sklearn', '0.19.1')

import sys
import time
import numpy as np

from sklearn.covariance import EllipticEnvelope

import util

outlier_frac = 0.05
ell = EllipticEnvelope(contamination=outlier_frac)
while True:
    X_train = util.receive_point_list_from_stdin()
    X_predict = util.receive_point_list_from_stdin()
    X_train, X_predict = util.nomalize_train_evaluate_data(X_train, X_predict)


    ell.fit(X_train)
    pred = ell.predict(X_predict)


    bools = pred == -1
    decisions = ell.decision_function(X_predict)

    util.send_bool_list_to_stdout(bools)
    util.send_double_list_to_stdout(decisions)

#!/ms/dist/python/PROJ/core/3.4.4/bin/python

import ms.version

ms.version.addpkg('numpy', '1.14.2')
ms.version.addpkg('scipy', '1.0.0')
ms.version.addpkg('sklearn', '0.19.1')

import sys
import time
import numpy as np

from sklearn import svm

import util

outlier_frac = 0.05
svm = svm.OneClassSVM(nu=outlier_frac, kernel='rbf', gamma=0.1)
while True:
    X_train = util.receive_point_list_from_stdin()
    X_predict = util.receive_point_list_from_stdin()
    X_train, X_predict = util.nomalize_train_evaluate_data(X_train, X_predict)


    svm.fit(X_train)
    pred = svm.predict(X_predict)

    bools = pred == -1
    decisions = np.squeeze(svm.decision_function(X_predict))

    util.send_bool_list_to_stdout(bools)
    util.send_double_list_to_stdout(decisions)

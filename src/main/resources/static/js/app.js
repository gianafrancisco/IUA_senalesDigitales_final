var senalesApp = angular.module('senalesApp', ['restangular']);
senalesApp.controller('LineInController', ['$scope', 'Restangular',
function($scope, Restangular) {
    var Filter = Restangular.all('linein/fir');
    var Signal = Restangular.all('linein/signal');
    Filter.getList().then(function(f) {
        $scope.filters = f;
    });
    var unKa = [0.002190587243836, 0.002853722845476, 0.004307867338426,  0.00674261549579,
                                                 0.010274321026,  0.01492796256296,   0.0206268971732,  0.02719183076969,
                                               0.03434945463871,  0.04175025099741,  0.04899405407214,  0.05566116091533,
                                               0.06134620074643,  0.06569165528868,  0.06841791053125,  0.06934701670932,
                                               0.06841791053125,  0.06569165528868,  0.06134620074643,  0.05566116091533,
                                               0.04899405407214,  0.04175025099741,  0.03434945463871,  0.02719183076969,
                                                0.0206268971732,  0.01492796256296,    0.010274321026,  0.00674261549579,
                                              0.004307867338426, 0.002853722845476, 0.002190587243836];
    $scope.coeficientes = JSON.stringify(unKa);


    $scope.agregar = function(){
        Filter.post(JSON.parse($scope.coeficientes)).then(function(){
            Filter.getList().then(function(f) {
                $scope.filters = f;
            });
        });
    };
    $scope.iniciar = function(){
        Signal.post();
    };
    $scope.parar = function(){
        Signal.remove();
    };
    $scope.borrar = function(){
        Restangular.all("linein").customDELETE("fir").then(function(){
             Filter.getList().then(function(f) {
                 $scope.filters = f;
             });
         });
    };
}]);

senalesApp.controller('AudioController', ['$scope', 'Restangular',
function($scope, Restangular) {
    var Filter = Restangular.all('file/fir');
    var Signal = Restangular.all('file/signal');
    Filter.getList().then(function(f) {
        $scope.filters = f;
    });
    var unKa = [0.002190587243836, 0.002853722845476, 0.004307867338426,  0.00674261549579,
                                                 0.010274321026,  0.01492796256296,   0.0206268971732,  0.02719183076969,
                                               0.03434945463871,  0.04175025099741,  0.04899405407214,  0.05566116091533,
                                               0.06134620074643,  0.06569165528868,  0.06841791053125,  0.06934701670932,
                                               0.06841791053125,  0.06569165528868,  0.06134620074643,  0.05566116091533,
                                               0.04899405407214,  0.04175025099741,  0.03434945463871,  0.02719183076969,
                                                0.0206268971732,  0.01492796256296,    0.010274321026,  0.00674261549579,
                                              0.004307867338426, 0.002853722845476, 0.002190587243836];
    $scope.coeficientes = JSON.stringify(unKa);

    $scope.agregar = function(){
        Filter.post(JSON.parse($scope.coeficientes)).then(function(){
            Filter.getList().then(function(f) {
                $scope.filters = f;
            });
        });
    };
    $scope.iniciar = function(){
        Signal.post();
    };
    $scope.parar = function(){
        Signal.remove();
    };
    $scope.borrar = function(){
        Restangular.all("file").customDELETE("fir").then(function(){
             Filter.getList().then(function(f) {
                 $scope.filters = f;
             });
         });
    };
}]);
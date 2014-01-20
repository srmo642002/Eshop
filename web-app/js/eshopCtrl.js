var eshop = angular.module('eshop', []);

eshop.controller('eshopCtrl', function ($scope, $http) {

    $scope.basketCounter = basketCounter;
    $scope.compareListCounter = compareListCounter;
    $scope.wishListCounter = wishListCounter;
    $scope.basket = basket;
    $scope.deliveryPrice = -1;
    $scope.compareList = compareList;
    $scope.wishList = wishList;
    $scope.wishListEnabled = wishListEnabled;
    $scope.contextRoot = contextRoot;

    //slides
    $scope.mainSlides = mainSlides;
    $scope.mainSlideSize = mainSlideSize;
    $scope.mainSlideWidth = mainSlideWidth;
    $scope.mainSlideHeight = mainSlideHeight;

    $scope.specialSaleSlides = specialSaleSlides;
    $scope.specialSaleSlideSize = specialSaleSlideSize;
    $scope.specialSaleSlideWidth = specialSaleSlideWidth;
    $scope.specialSaleSlideHeight = specialSaleSlideHeight;

//    product cart
    $scope.reloadProductCart = function (url, serializedData, productCard) {
        $http({
            url: url,
            method: "POST",
            data: serializedData,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function (data, status, headers, config) {
                productCard.html(data);
            }).error(function (data, status, headers, config) {
                console.error(
                    "The following error occured: " +
                        textStatus, errorThrown
                );
            });
    }
    $scope.reloadProductPrice = function (url, serializedData, productPrice) {
        $http({
            url: url,
            method: "POST",
            data: serializedData,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function (data, status, headers, config) {
                productPrice.html(data);
            }).error(function (data, status, headers, config) {
                console.error(
                    "The following error occured: " +
                        textStatus, errorThrown
                );
            });
    }

    //    basket
    $scope.addToBasket = function (id, name, price, selectedAddedValues) {
        $('#link-basket').addClass('full');

        $('#basketItems .rollbar-path-vertical').remove();
        $('#basketItems .rollbar-path-horizontal').remove();
        $('#basketItems .rollbar-content li').unwrap();
        $('#basketItems .scrollable').rollbar({zIndex: 80, wheelSpeed: 10});

        $scope.basketCounter++;
        var found = false;
        for (var i = 0; i < $scope.basket.length; i++) {
            if ($scope.basket[i].id == id) {
                $scope.basket[i].count++;
                found = true;
                break;
            }
        }
        if (!found)
            $scope.basket[$scope.basket.length] = {id: id, name: name, count: 1, realPrice: price};
        $http.post(contextRoot + "basket/add/" + id + "?addedValues=" + selectedAddedValues.toString()).success(function (response) {
        });
        return false;
    };

    $scope.removeFromBasket = function (id) {
        var found = false;
        for (var i = 0; i < $scope.basket.length; i++) {
            if ($scope.basket[i].id == id) {
                $scope.basketCounter -= $scope.basket[i].count;
                $scope.basket.splice(i, 1);
                found = true;
                break;
            }
        }
        if (found) {
            $http.post(contextRoot + "basket/remove/" + id).success(function (response) {
            });
        }
    };

    $scope.calculateBasketTotalPrice = function () {
        var totalPrice = 0;

        angular.forEach($scope.basket, function (item) {
            totalPrice += item.realPrice * item.count;
        });

        return totalPrice;
    }

    $scope.calculateBasketPayablePrice = function () {
        var totalPrice = 0;

        angular.forEach($scope.basket, function (item) {
            totalPrice += item.realPrice * item.count;
        });

        if ($scope.deliveryPrice != -1)
            totalPrice += $scope.deliveryPrice

        return totalPrice;
    }

    $scope.changeCount = function (id, count) {
        $http.post(contextRoot + "basket/changeCount/" + id + "?count=" + count).success(function (response) {
        });
    }

    //    compareList
    $scope.addToCompareList = function (id, name, price) {
        var found = false;

        if ($scope.compareListCounter == 0)
            $('#compareListItems .scrollable').rollbar({zIndex: 80, wheelSpeed: 10});

        for (var i = 0; i < $scope.compareList.length; i++) {
            if ($scope.compareList[i].id == id) {
                found = true;
                break;
            }
        }
        if (!found) {
            $scope.compareList[$scope.compareList.length] = {id: id, title: name, price: price};
            $scope.compareListCounter++
        }
        $http.post(contextRoot + "comparison/add/" + id).success(function (response) {
        });

        return false;
    };

    $scope.removeFromCompareList = function (id) {
        var found = false;
        for (var i = 0; i < $scope.compareList.length; i++) {
            if ($scope.compareList[i].id == id) {
                $scope.compareListCounter -= 1;
                $scope.compareList.splice(i, 1);
                found = true;
                break;
            }
        }
        if (found) {
            $http.post(contextRoot + "comparison/remove/" + id).success(function (response) {
            });
        }
    };

    //    wishList
    $scope.addToWishList = function (id, name, price) {
        if (!$scope.wishListEnabled) {
            $.msgGrowl({
                type: 'warning', 'text': wishListNotEnabledMessage, lifetime: 5000
            });
            return;
        }

        if ($scope.wishListCounter == 0)
            $('#wishListItems .scrollable').rollbar({zIndex: 80, wheelSpeed: 10});

        var found = false;
        for (var i = 0; i < $scope.wishList.length; i++) {
            if ($scope.wishList[i].id == id) {
                found = true;
                break;
            }
        }
        if (!found) {
            $scope.wishListCounter++;
            $scope.wishList[$scope.wishList.length] = {id: id, title: name, price: price};
        }
        $http.post(contextRoot + "wishList/add/" + id).success(function (response) {
        });

        return false;
    };

    $scope.removeFromWishList = function (id) {
        var found = false;
        for (var i = 0; i < $scope.wishList.length; i++) {
            if ($scope.wishList[i].id == id) {
                $scope.wishListCounter -= 1;
                $scope.wishList.splice(i, 1);
                found = true;
                break;
            }
        }
        if (found) {
            $http.post(contextRoot + "wishList/remove/" + id).success(function (response) {
            });
        }
    };

//    common functions
    $scope.isEmpty = function (value) {
        return !value || value.toString().length == 0;
    }
});

function updateBasketItemCount(id, count) {
    var scope = angular.element(document.getElementById('main-container')).scope();

    var found = false;
    for (var i = 0; i < scope.basket.length; i++) {
        if (parseInt(id) == scope.basket[i].id) {
            scope.basket[i].count = count;
            scope.$apply();
            found = true;
        }
    }

    if (found)
        scope.changeCount(id, count);
}

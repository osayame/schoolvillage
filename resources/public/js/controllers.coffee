require "./angular"
require "./angular-sanitize"

HOST = window.DDCONF?.HOST || ""
DEMO = window.DDCONF?.DEMO?

angular.module("doubledonation_register", [])

.controller "RegisterController", ($scope, $http) ->
    if DDCONF.PARTNER is "donorpro"
        $scope.donorpro = {
            donorpro_annually: "Billed $399 annually"
            donorpro_twoyears: "Billed $678 every 2 years"
        }
        initial_plan = "donorpro_annually"
    else if DDCONF.PARTNER is "clickandpledge"
        $scope.clickandpledge = {plan: "clickandpledge_annually"}
        initial_plan = "clickandpledge_annually"
    else
        if prefilled_plan = window.location.search?.match(/\?plan=(\w+)/)?[1]
            $scope.prefilled = true
        initial_plan = prefilled_plan || "small_199_annually"

    $scope.register_form = {
        plan: initial_plan
        cc_info: {}
    }

    $scope.plans = {
        small_199_annually: "Hosted Plan at $199/year"
        pro_399_annually: "Embedded Plan at $399/year"
        silver_699_annually: "Tier 3 at $699/year"
        small_338_twoyears: "Tier 1 at $169/year for 2 years (billed upfront)"
        pro_678_twoyears: "Tier 2 at $339/year for 2 years (billed upfront)"
        silver_1198_twoyears: "Tier 3 at $599/year for 2 years (billed upfront)"
    }

    $scope.submitForm = (form, valid) ->
        $scope.register_form.submitted = true
        $scope.errors = null
        if valid
            $scope.button_disabled = true
            req = $http.post("/api/register", $scope.register_form)
            req.success ->
                location.href = "https://doublethedonation.com/members/home"
                return false
            req.error (data) ->
                $scope.button_disabled = false
                $scope.errors = data

angular.element(document).ready ->
    register_form = document.querySelector(".doubledonation-register")
    angular.bootstrap(register_form, ['doubledonation_register']) if register_form

(ns event-bus.core)

(defn register [event-bus-host on-connect]
  (js/console.log (str "Registering event bus on: " event-bus-host))
  (let [eb (js/EventBus. event-bus-host)]
       (.enableReconnect eb true)
       (set! (.-onopen eb)  (fn []
                                (prn (str "Can register to: " event-bus-host))
                                (on-connect  eb)))
       eb))

(defn register-handler [e-bus address handler-fn]
  (prn "regiter handler for " address)
  (.registerHandler e-bus address handler-fn)
  e-bus)

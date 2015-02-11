(ns sprint-poker.core
  (:require [clojure.data.json :as json]
            [clojure.algo.generic.functor :refer [fmap]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.server.standalone :refer [serve]]
            [ring.util.response :refer [content-type redirect response status]])
  (:gen-class :main true))

(def data (atom {:revealed false :votes {}}))

(defn get-votes
  "Return a map of all votes."
  [req]
  (println "get-votes" @data)
  (if (:revealed @data)
    (-> (response (json/write-str (:votes @data)))
        (content-type "application/json"))
    (-> (response (json/write-str (fmap (fn [v] "-") (:votes @data))))
        (content-type "application/json"))))

(defn put-vote
  [{{client-id "client-id" points "points"} :params :as req}]
  {:pre [(string? client-id) (string? points)]}
  (println "put-vote" client-id points)
  (swap! data #(assoc-in % [:votes client-id] points))
  (get-votes req))

(defn reveal-votes
  [req]
  (println "reveal-votes")
  (swap! data #(assoc-in % [:revealed] true))
  (get-votes req))

(defn reset-votes
  [req]
  (println "reset-votes")
  (reset! data {:revealed false :votes {}})
  (get-votes req))

(defn redirect-to
  [url]
  (fn [req]
    (response "")
    (redirect url)))

(defn app
  [req]
  ; Teh most advanced router evar!!1one!!!
  ((or (and (= :get (:request-method req))
            (= "/" (:uri req))
            (redirect-to "/index.html"))
       (and (= :get (:request-method req))
            (= "/votes" (:uri req))
            get-votes)
       (and (= :put (:request-method req))
            (= "/vote" (:uri req))
            put-vote)
       (and (= :post (:request-method req))
            (= "/reveal" (:uri req))
            reveal-votes)
       (and (= :post (:request-method req))
            (= "/reset" (:uri req))
            reset-votes)
       (throw (IllegalArgumentException. (str "Unmatched route: "
                                              (:request-method req)
                                              " "
                                              (:uri req)))))
     req))

(def handler
  (-> app
      (wrap-params)
      (wrap-resource "/")
      (wrap-file-info)))

(defn -main [] (serve handler))

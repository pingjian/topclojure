(ns template)

(defn {{function}}
  [{% for p in parameters %}{% if not forloop.first %} {% endif %}{{p}}{% endfor %}]
  )
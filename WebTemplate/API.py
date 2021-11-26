from flask import Flask, render_template

app = Flask(__name__)

# So basically all we have to do is create app.route URLS, then a function for each URL
# These functions can just be calls to external functions elsewhere with some data passing
# see https://www.imaginarycloud.com/blog/flask-python/ for a tutorial on how this all works

@app.route("/")
def home_page():
    return render_template('HomePage.html')

@app.route("/test")
def test():
    return "done it"

@app.route("/mod")
def moderation():
    return "moderation page"
@app.route("/screen")
def screenTime():
    return "screen time page"

@app.route("/semantics")
def semantics():
    return "semantic analysis page"

if __name__ == "__main__":
    app.run()

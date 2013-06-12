import os
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt


@csrf_exempt
def upload(request):
    print u"Uploading?!"
    print request.FILES
    print request.FILES.keys()
    print request.FILES.values()
    if request.FILES.has_key("file"):
        print request.FILES["file"]
        with open(os.path.join(os.path.dirname(os.path.realpath(__file__)), request.FILES["file"].name), "wb") as f:
            for chunk in request.FILES["file"].chunks():
                f.write(chunk)
        return HttpResponse("Success!")
    return HttpResponse("No file!")


def testUpload(request):
    return HttpResponse('<html><body><form action="/upload/" method="post" enctype="multipart/form-data"> '
                        '<input type="file" name="file" /><input type="submit" /></form></body></html>')
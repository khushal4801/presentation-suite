from fastapi import FastAPI, HTTPException
from fastapi.responses import FileResponse
from pydantic import BaseModel
from gtts import gTTS
import os

app = FastAPI()

class TTSRequest(BaseModel):
    text: str

@app.post("/generate-tts")
async def generate_tts(request: TTSRequest):
    try:
        print(f"Generating TTS for text: {request.text}")
        output_file = "tts_output.mp3"
        tts = gTTS(text=request.text, lang='en')
        tts.save(output_file)

        return FileResponse(
            path=output_file,
            media_type="audio/mpeg",
            filename=output_file
        )

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

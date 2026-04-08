<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mi Página Personal</title>
    <style>
        /* ESTILO DEL MENÚ - IGUAL A TU IMAGEN */
        body { 
            background-color: #0b0e14; 
            color: white; 
            font-family: sans-serif; 
            margin: 0; 
            padding: 0; 
        }
        
        nav { 
            background-color: #161b22; 
            padding: 20px; 
            display: flex; 
            gap: 15px; 
            justify-content: center; 
            align-items: center;
        }

        /* ESTE ES EL BOTÓN QUE CORREGIMOS */
        .btn-naranja {
            padding: 12px 35px;
            border-radius: 50px;
            text-decoration: none;
            color: white;
            background-color: #eb4f27; /* Naranja exacto */
            font-weight: bold;
            font-size: 18px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .btn-gris {
            padding: 12px 30px;
            border-radius: 50px;
            text-decoration: none;
            color: #8b949e;
            background-color: #21262d;
            font-weight: bold;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        /* ÁREA DE JUEGOS */
        .contenedor-juegos { text-align: center; padding: 40px; }
        canvas { border: 5px solid #eb4f27; background: #000; border-radius: 10px; }
    </style>
</head>
<body>

    <nav>
        <a href="#" class="btn-gris">🕒 Linea de Tiempo</a>
        <a href="#" class="btn-gris">📸 Galeria</a>
        <a href="#seccion-juegos" class="btn-naranja">🎮 Juegos</a> 
        <a href="#" class="btn-gris">📱 Redes</a>
    </nav>

    <div id="seccion-juegos" class="contenedor-juegos">
        <h1 style="color: #eb4f27;">MIS JUEGOS</h1>
        
        <div style="margin-bottom: 50px;">
            <h3>Escena Final de Mario</h3>
            <div style="position: relative; display: inline-block;">
                <div id="status" style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); font-size: 50px; font-weight: bold; pointer-events: none;"></div>
                <canvas id="marioCanvas" width="800" height="600"></canvas>
            </div>
        </div>

        <hr style="border: 1px solid #333; width: 80%; margin: 40px auto;">

        <div>
            <h3>Juego de Avión</h3>
            <object data="JuegoAvion.jar" type="application/x-java-applet" width="800" height="400">
                <param name="archive" value="JuegoAvion.jar">
            </object>
        </div>
    </div>

    <script>
        // LÓGICA MARIO
        const canvas = document.getElementById("marioCanvas");
        const ctx = canvas.getContext("2d");
        const statusTxt = document.getElementById("status");

        const img = {
            m: new Image(), b: new Image(), lm: new Image(), 
            lg: new Image(), f: new Image(), h: new Image(), c: new Image()
        };
        
        // Rutas de tus archivos
        img.m.src = "imagen de mario.png";
        img.b.src = "imagen de Bowser.png";
        img.lm.src = "imagen ladrillo marron.png";
        img.lg.src = "imagen ladrillo gris.png";
        img.f.src = "imagen lava.png";
        img.h.src = "imagen Hacha.png";
        img.c.src = "imagen cadena pequeña.png";

        let mario = { x: 150, y: 360, w: 60, h: 90, vY: 0, saltando: false, d: 1 };
        let bowser = { x: 600, y: 350, w: 110, h: 110, v: 1.3, d: -1 };
        let camX = 0, puente = true, estado = 0;
        const hX = 1850;
        const teclas = {};

        window.addEventListener("keydown", e => { teclas[e.code] = true; if(e.code==="Space") e.preventDefault(); });
        window.addEventListener("keyup", e => teclas[e.code] = false);

        function gameLoop() {
            if (estado === 0) {
                if (teclas["ArrowLeft"]) { mario.x -= 6; mario.d = -1; }
                if (teclas["ArrowRight"]) { mario.x += 6; mario.d = 1; }
                if ((teclas["Space"] || teclas["ArrowUp"]) && !mario.saltando) { mario.vY = -16; mario.saltando = true; }
                mario.y += mario.vY;
                if (mario.y < 360) mario.vY += 0.8; else { mario.y = 360; mario.vY = 0; mario.saltando = false; }
                camX = Math.max(0, Math.min(mario.x - 200, 1200));
                if (bowser.x < mario.x - 20) { bowser.x += bowser.v; bowser.d = 1; }
                else if (bowser.x > mario.x + 20) { bowser.x -= bowser.v; bowser.d = -1; }
                if (mario.x + 40 > hX - 20 && mario.y > 340) { estado = 2; puente = false; statusTxt.innerText = "¡GANASTE!"; statusTxt.style.color = "yellow"; }
                if (puente && Math.abs(mario.x - bowser.x) < 50 && Math.abs(mario.y - bowser.y) < 60) { estado = 1; statusTxt.innerText = "GAME OVER"; statusTxt.style.color = "red"; }
            }
            ctx.clearRect(0, 0, 800, 600);
            if (img.f.complete) ctx.drawImage(img.f, 0, 0, 800, 600);
            ctx.save(); ctx.translate(-camX, 0);
            for (let y = 320; y < 600; y += 40) ctx.drawImage(img.lg, hX + 40, y, 40, 40);
            if (puente) {
                for (let i = 0; i < hX + 40; i += 40) ctx.drawImage(img.lm, i, 450, 40, 40);
                ctx.drawImage(img.c, hX - 20, 360, 70, 50);
                ctx.drawImage(img.h, hX - 15, 405, 45, 45);
            }
            ctx.save();
            if (bowser.d === 1) { ctx.translate(bowser.x + bowser.w, bowser.y); ctx.scale(-1, 1); ctx.drawImage(img.b, 0, 0, bowser.w, bowser.h); }
            else ctx.drawImage(img.b, bowser.x, bowser.y, bowser.w, bowser.h);
            ctx.restore();
            if (estado !== 1) {
                ctx.save();
                if (mario.d === -1) { ctx.translate(mario.x + mario.w, mario.y); ctx.scale(-1, 1); ctx.drawImage(img.m, 0, 0, mario.w, mario.h); }
                else ctx.drawImage(img.m, mario.x, mario.y, mario.w, mario.h);
                ctx.restore();
            }
            ctx.restore();
            requestAnimationFrame(gameLoop);
        }
        gameLoop();
    </script>
</body>
</html>
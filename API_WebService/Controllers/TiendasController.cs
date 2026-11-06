using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

[Route("api/[controller]")]
[ApiController]
public class TiendasController : ControllerBase
{
    private readonly BDContexto _context;
    public TiendasController(BDContexto context)
    {
        _context = context;
    }

    // GET: api/Tiendas
    [HttpGet]
    public async Task<ActionResult<IEnumerable<Tienda>>> GetTiendas()
    {
        var lista = await _context.Tiendas.ToListAsync();
        var dtos = lista.Select(t => new Tienda
        {
            IdTienda = t.IdTienda,
            Nombre = t.Nombre,
            Ubicacion = t.Ubicacion,
            DistanciaKm = t.DistanciaKm,
            Foto = t.Foto,
            Ruta = GetRutaUrl(t)
        });

        return Ok(dtos);
    }

    private string GetRutaUrl(Tienda t)
    {
        if (!string.IsNullOrEmpty(t.Ruta))
            return $"{Request.Scheme}://{Request.Host}{Request.PathBase}/Imagenes/{t.Foto}";
        return null;
    }

    // POST: api/Tiendas
    [HttpPost]
    public async Task<ActionResult<Tienda>> PostTienda([FromForm] Tienda tienda)
    {
        var ruta = string.Empty;
        if (tienda.Archivo != null && tienda.Archivo.Length > 0)
        {
            var nombreFoto = Guid.NewGuid().ToString() + ".jpg";
            ruta = "Imagenes/" + nombreFoto;

            // Asegurarse de que la carpeta exista
            if (!Directory.Exists("Imagenes"))
            {
                Directory.CreateDirectory("Imagenes");
            }

            using (var stream = new FileStream(ruta, FileMode.Create))
            {
                await tienda.Archivo.CopyToAsync(stream);
                tienda.Foto = nombreFoto;
                tienda.Ruta = ruta;
            }
        }
        _context.Tiendas.Add(tienda);
        await _context.SaveChangesAsync();

        return CreatedAtAction("GetTiendas", new { id = tienda.IdTienda }, tienda);
    }
}
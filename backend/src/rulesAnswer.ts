/*
 * Base para DM, package 8: rules questions answered with AI (D-0041, D-0070, P4).
 *
 * The App searches the bundled Spanish SRD on the device (works offline) and sends the question
 * with the passages it found. The Worker asks a replaceable model (Cloudflare Workers AI) to answer
 * in Spanish using only those passages, citing them as [1], [2]… The App always shows the exact SRD
 * text next to the answer. Without the AI binding the route answers 503 and the App keeps its
 * offline search.
 */

/** The part of the Workers AI binding this Worker uses. */
export interface AiRunner {
  run(model: string, input: unknown): Promise<unknown>;
}

export interface RulesPassage {
  version: string;
  page: number;
  heading: string;
  text: string;
}

export const RULES_QUESTION_MAX_CHARS = 500;
export const RULES_PASSAGES_MAX = 8;
export const RULES_PASSAGE_MAX_CHARS = 1500;
/** Replaceable (D-0070): set AI_MODEL to change it without code changes. */
export const DEFAULT_RULES_MODEL = "@cf/meta/llama-3.1-8b-instruct-fp8";

const SYSTEM_PROMPT = [
  "Eres un asistente de reglas de Dungeons & Dragons para el Director de Juego.",
  "Responde en español, en pocas frases claras.",
  "Usa solamente los fragmentos del SRD que se te entregan, numerados [1], [2]…, y cita el número de cada fragmento que uses.",
  "Si los fragmentos no alcanzan para responder, dilo y sugiere qué buscar. No inventes reglas.",
  "El DM decide en la mesa: no impongas, explica lo que dice el texto.",
].join(" ");

export class RulesAnswerer {
  private readonly ai: AiRunner;
  private readonly model: string;

  constructor(ai: AiRunner, model: string = DEFAULT_RULES_MODEL) {
    this.ai = ai;
    this.model = model;
  }

  async answer(question: string, passages: RulesPassage[]): Promise<string> {
    const context = passages
      .map((p, i) => `[${i + 1}] SRD ${p.version}, p. ${p.page}${p.heading ? ` · ${p.heading}` : ""}\n${p.text}`)
      .join("\n\n");
    const result = await this.ai.run(this.model, {
      messages: [
        { role: "system", content: SYSTEM_PROMPT },
        { role: "user", content: `Fragmentos del SRD:\n\n${context}\n\nPregunta: ${question}` },
      ],
      max_tokens: 512,
      temperature: 0.2,
    });
    const text = extractText(result);
    if (text == null || text.trim().length === 0) {
      throw new Error("The rules model returned no text.");
    }
    return text.trim();
  }
}

function extractText(result: unknown): string | null {
  if (typeof result === "string") return result;
  if (result != null && typeof result === "object") {
    const response = (result as Record<string, unknown>).response;
    if (typeof response === "string") return response;
  }
  return null;
}
